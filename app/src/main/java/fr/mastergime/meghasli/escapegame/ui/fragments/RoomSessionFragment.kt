package fr.mastergime.meghasli.escapegame.ui.fragments

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fr.mastergime.meghasli.escapegame.Notifications.createChannel
import fr.mastergime.meghasli.escapegame.Notifications.sendNotificationUpdateDone
import fr.mastergime.meghasli.escapegame.R
import fr.mastergime.meghasli.escapegame.ThreadServer.MyBluetoothService
import fr.mastergime.meghasli.escapegame.databinding.FragmentRoomSessionBinding
import fr.mastergime.meghasli.escapegame.model.BluetoothDeviceContent
import fr.mastergime.meghasli.escapegame.model.UserForRecycler
import fr.mastergime.meghasli.escapegame.model.UsersListAdapter
import fr.mastergime.meghasli.escapegame.services.BluetoothService
import fr.mastergime.meghasli.escapegame.viewmodels.BluetoothViewModel
import fr.mastergime.meghasli.escapegame.viewmodels.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.content.ContextCompat.getSystemService
import android.app.ActivityManager




@AndroidEntryPoint
class RoomSessionFragment : Fragment() {
    /*********************************** bluetooth ******************************************/
    private lateinit var mBluetoothDevice : BluetoothDevice
    private val TAG_PAIRED = "DEVICE_PAIRED"
    private val TAG_UNPAIRED = "DEVICE_UNPAIRED"
    val REQUEST_ENABLE_BT = 1
    val REQUEST_ENABLE_DISC = 2
    // le nom de notre serveur
    lateinit var serverNameDevice : String
    // stocker Mac adresse en parcourant la liste device bluetooth
    lateinit var adressMacServer : String
    // filter et prendre que BluetoothDevice.ACTION_FOUND
    lateinit var filter : IntentFilter
    //
    lateinit var bluetoothAdapter: BluetoothAdapter

    // liste contenant les mac bluetooth devices
    var listBluetooth = mutableListOf<BluetoothDeviceContent>()

    var isserver = false

    private val bluetoothViewModel: BluetoothViewModel by activityViewModels()
    /*********************************** fin bluetooth ************************************/


    private lateinit var binding: FragmentRoomSessionBinding
    private val sessionViewModel: SessionViewModel by viewModels()
    lateinit var dialog : AlertDialogFragment

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }

    @Inject
    lateinit var mediaPlayerFactory: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* var sessionName = arguments?.get("sessionName")
         Log.d("sessionName", sessionName as String)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomSessionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableStatusBar()

        dialog = AlertDialogFragment(clickListener = {
            dialog.dismiss()

        }, clickListener2 = {
            dialog.dismiss()
            binding.quitButton.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            binding.quitButton.isEnabled = false
            sessionViewModel.quitSession()
        })

        sessionViewModel.updateSessionId()
        sessionViewModel.sessionId.observe(viewLifecycleOwner) {
            sessionId = it
        }

        sessionViewModel.updateUsersList()
        sessionViewModel.launchSession()


        var usersList = mutableListOf(
            UserForRecycler("Adding Users ...", false),
            UserForRecycler("", false),
            UserForRecycler("", false),
            UserForRecycler("", false),
            UserForRecycler("", false),
            UserForRecycler("", false)
        )

        var usersListAdapter = UsersListAdapter()
        usersListAdapter.submitList(usersList)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = usersListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.button.setOnClickListener() {
            binding.progressBar.visibility = View.VISIBLE
            it.isEnabled = false
            sessionViewModel.readyPlayer()
        }

        binding.quitButton.setOnClickListener() {
            showQuitDialog()
            /***code bluetooth**/
            try{
                //arreter le service si user quitte la session
                activity?.stopService(Intent(context, BluetoothService::class.java))
            }catch(ex : Exception ){
                Toast.makeText(context, requireContext().getString(R.string.error), Toast.LENGTH_SHORT).show()
            }

            /***end code bluetooth**/
        }




        sessionViewModel.userNameList.observe(viewLifecycleOwner) { value ->
            if (value.isNotEmpty())
                usersListAdapter.submitList(value)
            else
                sessionViewModel.getUsersList()
        }

        sessionViewModel.launchSessionState.observe(viewLifecycleOwner) { value ->
            observeLunchSessionState(value)
        }


        sessionViewModel.sessionState.observe(viewLifecycleOwner) { value ->
            observeSessionState(value)
        }

        sessionViewModel.quitSessionState.observe(viewLifecycleOwner) { value ->
            observeQuiteSessionState(value)
        }

        sessionViewModel.readyPlayerState.observe(viewLifecycleOwner) { value ->
            if (value == "Success") {
                binding.progressBar.visibility = View.INVISIBLE
            } else {
                binding.button.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(
                    activity, "Please retry can't make you ready",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        sessionViewModel.btServerDeviceName.observe(viewLifecycleOwner){value ->
        }


        /************************************** bluetooth *********************************/
        //bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var mBluetoothManager:BluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = mBluetoothManager.getAdapter()

        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Does not support BlueTooth", Toast.LENGTH_SHORT).show()
        }

        // il n'a pas besoin d'activer le  bluetooth

        //Log.d("TEST_MAC","debut "+sessionViewModel.readNameServerBluetoothOnFirebase())
        sessionViewModel.readNameServerBluetoothOnFirebase()
        sessionViewModel.btServerDeviceName.observe(viewLifecycleOwner ,{
            //  si la fonction firebase retourne une exception
            try{
                // readNameServerBluetoothOnFirebase() s'excute deux fois
                    // tjrs "" dans le premier résultat puis le resultat souhaité
                if(it!=""){
                    when(it){
                            // firebase nous retourne une execption quand
                            // la valeur est null dans la BD
                        "Exception" ->{
                            // bloc executé seulement dans la premiere fois (création de la session)
                            // ecrire dans firebase seulement si le champ dans firebase est vide
                            sessionViewModel.writeNameServerBluetoothOnFirebase(bluetoothAdapter.name)
                            binding.startServer.visibility = View.VISIBLE
                            binding.joinServer.visibility = View.INVISIBLE
                            isserver = true
                        }
                        bluetoothAdapter.name -> {
                            // bloc executé quand le serveur revient à l'appli.
                            binding.startServer.visibility = View.VISIBLE
                            binding.joinServer.visibility = View.INVISIBLE
                            isserver = true
                        }
                        else -> {
                            // bloc executé quand le client entre à la session n'import quand
                            serverNameDevice = it
                            binding.startServer.visibility = View.INVISIBLE
                            binding.joinServer.visibility = View.VISIBLE
                        }
                    }
                }
            }
            catch(Ex : Exception){
                Toast.makeText(context, requireContext().getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        })


        /*************************/

        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery();
        }

        filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val requestPermissionLauncher =
            registerForActivityResult (
                ActivityResultContracts.RequestPermission ()
            ) { isGranted : Boolean ->
                if ( isGranted ) {
                    /*****possiblité 1 :accepté********/
                    // la permission vient etre donnée
                    //Toast.makeText(context, "Autorisation vient être donnée pour localisation", Toast.LENGTH_SHORT).show()
                    /******autorisation pour 1er fois*******/
                    //executer ce code ssi GPS est activé
                    if(islocationEnabled()){
                        /// pour que être discovrable
                        val discoverableIntent: Intent =
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                            }
                        startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISC)
                    }////Afficher toast pour l'inviter à activer le GPS
                    else{
                        Toast.makeText(context, requireContext().getString(R.string.ask_for_enable_gps), Toast.LENGTH_SHORT).show()
                    }
                    /*****possiblité 2 :refusé********/
                }// on aura pas besoin de else puisque le client cliquera sur le bouton scan
            }
        binding.scanButton.setOnClickListener{
            /*
             * pour le serveur n'as pas besoin de scan (alors localisation) mais d'être discovrable
             */
            if(isserver){
                /// pour être discovrable
                val discoverableIntent: Intent =
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                    }
                startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISC)
                Toast.makeText(context, requireContext().getString(R.string.discovarability), Toast.LENGTH_SHORT).show()
            }/* android 10 a besoin de permission location pour bluetooth scan**/
            /*
            * si Android 10 il aura besoin d'activer GPS puis
            *   if      : scanner pour trouver le device bluetooth
            *   else    : sinon  scanner pour trouver le device bluetooth
            * */
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    when {
                        ContextCompat.checkSelfPermission (
                            requireContext () ,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED -> // permission déjà donnée (4 -> 8a)
                        {
                            //executer ce code ssi GPS est activé
                            /******code autorisation pour x>1er fois*******/
                            if(islocationEnabled()){
                                /// pour que être discovrable
                                val discoverableIntent: Intent =
                                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                                        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                                    }
                                startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISC)
                                Toast.makeText(context, "Scan ...", Toast.LENGTH_SHORT).show()
                            }////Afficher toast pour l'inviter à activer le GPS
                            else{
                                Toast.makeText(context, requireContext().getString(R.string.ask_for_enable_gps), Toast.LENGTH_SHORT).show()
                            }
                            /******fin code autorisation pour x>1er fois*******/
                        }
                        shouldShowRequestPermissionRationale (
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) -> // l’ application devrait donner une explication (5a -> 5b)
                        {
                            Toast.makeText(context, "impossible de scanner !!", Toast.LENGTH_SHORT).show()
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        // Avant de demander la permission
                        else -> // Demander la permission (6)
                        {
                            Toast.makeText(context, "l'autorisation GPS est requis.", Toast.LENGTH_SHORT).show()
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }else{
                    val discoverableIntent: Intent =
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                        }
                    startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISC)
                }
                Toast.makeText(context, requireContext().getString(R.string.scan), Toast.LENGTH_SHORT).show()
            }
        }

        // passer le viewmodel au service
        myBluetoothService = MyBluetoothService(bluetoothViewModel,requireContext())

        /**
         * TODO
         */
        /* on click button : server side
            dès la creation de la session
            1. stocker dans firebase (nom session , device name server)
                1.1 tester si le nom n'est pas vide
                1.2 si le nom est bien stocker en firebase
        */
        //Log.d("BlueTooth_device_name" , bluetoothAdapter.name)
        // UUID = from(nomsession)
        // on va filter les server uuid = liste(adressmac)
        //
        /* on click button : client side
            directement recuperer le nom !
            2. à partir de nom de session :
                2.1 récuperation name device server from firebase
                2.2 récuperation device mac adresse from devices list via device name
        */
        /*
            ce nom doit etre recuprer de firebase
        */

        /*
                Create channel for notification
        */

        notificationManager = ContextCompat.getSystemService(
            requireContext(),
            NotificationManager::class.java
        )!!
        notificationManager?.createChannel(requireContext())

        // changer le texte + isnot clickable "serveur n'est pas encore disponible"
        // isInListe()
        binding.joinServer.setOnClickListener {
            // parcourir la liste des devices bluetooth
            // recuperation MAC
            if(bluetoothAdapter.state == BluetoothAdapter.STATE_ON){
                if(listBluetooth.size!=0){
                    // recuperer le nom de device server bluetooth dans la liste resultat de scan
                        // retourne caractere vide "" si nom bluetooth n'existe pas dans la liste
                    adressMacServer = getMacAddress(listBluetooth, serverNameDevice)
                    Log.d("TROUVER_MAC:",adressMacServer)
                    //val mBluetoothDevice = bluetoothAdapter!!.getRemoteDevice("C4:9F:4C:79:1E:66")
                    if(adressMacServer == ""){
                        Toast.makeText(context, requireContext().getString(R.string.server_unavailable), Toast.LENGTH_LONG).show()
                    }else{
                        mBluetoothDevice = bluetoothAdapter.getRemoteDevice(adressMacServer)
                        //Log.d("mac_found", adressMacServer)
                        try {
                            /**
                             *
                             *   j'ai serveur dans la liste bluetooth mais le serveur n'est pas lancé.
                             */
                            myBluetoothService.tryConnect(mBluetoothDevice)
                            Toast.makeText(context, requireContext().getString(R.string.server_available), Toast.LENGTH_SHORT).show()
                        }catch(ex : Exception){
                            Toast.makeText(context, requireContext().getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    // condition garde fou, il n'est jamais executé sauf si un problème dans
                        // une version ulterieure d'android audessus de 10
                    Toast.makeText(context, requireContext().getString(R.string.ask_for_scan), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, requireContext().getString(R.string.ask_for_scanner_activer), Toast.LENGTH_LONG).show()
            }
        }

        /**
         * TODO
         * 1. Done : Impossible de join server si le server n'est pas discovrable (agir sur button )
         * 2. Done : Service problème
         * 3. Done : Test if GPS is enabled for android 10
         */
        binding.startServer.setOnClickListener{
            // faire attendre le serveur le temps que sessionId se rempli
            if(sessionId!=""){
                if(isServiceBluetoothRunning(BluetoothService::class.java)){
                    Toast.makeText(context, requireContext().getString(R.string.is_running), Toast.LENGTH_LONG).show()
                }else{
                    // passer ici ssi le bluetooth est activé
                    if(bluetoothAdapter.state == BluetoothAdapter.STATE_ON){
                        activity?.registerReceiver(receiver, IntentFilter("GET_SIGNAL_STRENGTH"))
                        // passer la session au viewmodel puis au service pour l'utiliser
                            // dans la notification resolution énigme
                        bluetoothViewModel.listenThenAccept(requireContext(), sessionId)
                    }else{
                        Toast.makeText(context, requireContext().getString(R.string.ask_for_enable_bluetooth), Toast.LENGTH_LONG).show()
                    }
                }
            }else{
                Toast.makeText(context, "Merci de réessayer", Toast.LENGTH_LONG).show()
            }
        }
        activity?.registerReceiver(receiver, IntentFilter("MESSAGE_FROM_SERVER"))
        // le client n'arrive pas à trouver le serveur
        activity?.registerReceiver(receiver, IntentFilter("EXCEPTION_SERVEUR_UNAVALAIBLE"))
        /**
         * TODO
        1.  done : régler le problème de UI fige (run??)
        (6) test if thread is already working once user click an other time
        2.  done : passer de l'information de thread vers service (observer).
        done : informer l'user en état de listening (broadcast receiver)
        3.  envoie de la notification.
        3.1 done : connection établie
        3.2 pour broadcast (changement d'état)
        3.  in progress : Quand le service crée doit pas etre recréer.
        4.  done : arreter le service si user decide sur la notification.
        on arrete -> stream socket thread
        5.  done : il faut pas réappeler server
        6.  done : il faut pas réappeler client
        7.  (session=nameserver)!=currentnameServer -> afficher que le button
        8. client clique sur joindre alors que le serveur n'a pas encore commencé.
            Solution : (ajouter à firebase une variable qui identifie si le serveur est crée)

        -----------------------
        scénarios à prendre en compte
        - done : Si le serveur a quitté/desactiver bluetooth client a tjrs MAC , il qlique sur rejoindre -> resultat ->
        - done : Si le client desactive le bluetooth appuie sur rejoindre
        - done : Si le client clique deux fois sur scan ou sur rejoindre
        - done : Si le serveur desactive le bluetooth appuie sur scan ou "lancer serveur"
         */
        /******** end code bluetooth *********/

    }



    private fun observeLunchSessionState(value: String?) {
        if (value == "Success") {
            lifecycleScope.launch(Dispatchers.Main) {
                if (sessionViewModel.getSessionIdFromUser() != "null" &&
                    findNavController().currentDestination?.label == "fragment_session_room"
                ) {
                    loadAnimationSignUpDone()
                }
            }
        } else if (value == "Waiting for other Players")
            Toast.makeText(
                activity, "Waiting for others...",
                Toast.LENGTH_SHORT
            ).show()
        else if(value == "Failed")
            Toast.makeText(
                activity, "Can't launch Session, please retry",
                Toast.LENGTH_SHORT
            ).show()
        else if(value == "Unknown Error"){
            Toast.makeText(
                activity, "Unknown Error, please retry",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun observeQuiteSessionState(value: String?) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (value == "Success") {
                sessionViewModel.notReadyPlayer()
                findNavController().navigate(R.id.action_sessionRoomFragment_to_menuFragment)
            } else
                Toast.makeText(
                    activity, "Can't leave Session please retry",
                    Toast.LENGTH_SHORT
                ).show()
            binding.progressBar.visibility = View.INVISIBLE
            binding.quitButton.isEnabled = true
        }
    }


    private fun observeSessionState(value: Boolean?) {
        if (value == true) {
            findNavController().navigate(R.id.action_sessionRoomFragment_to_gameFragment)
        }
    }

    private fun disableStatusBar() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun loadAnimationSignUpDone() {
        binding.animationViewLoading.setAnimation("count_down.json")
        binding.animationViewLoading.visibility = View.VISIBLE
        binding.animationViewLoading.playAnimation()
        binding.animationViewLoading.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                binding.quitButton.visibility = View.INVISIBLE
                binding.button.visibility = View.INVISIBLE
                binding.textViewExit.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {
                //sessionViewModel.starTimerSession()
                //  sessionViewModel.getStarterSession()
                // findNavController().navigate(R.id.action_sessionRoomFragment_to_gameFragment)
                ioScope.launch {
                    delay(1000)
                }
                try{
                    findNavController().navigate(R.id.action_sessionRoomFragment_to_gameFragment)
                }catch(ex : IllegalArgumentException){
                }
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    private fun observeTimeStarter() {

    }

    private fun showQuitDialog() {

        dialog.show(parentFragmentManager, "")

    }

    override fun onResume() {
        super.onResume()
        disableStatusBar()
        if(!mediaPlayerFactory.isPlaying){
            mediaPlayerFactory.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if(mediaPlayerFactory.isPlaying){
            mediaPlayerFactory.stop()
        }
    }

    companion object {
        //
        lateinit var notificationManager : NotificationManager

        var sessionId =""
        /**** code bluetooth****/
        // classe prenant en param requireContext
        lateinit var myBluetoothService: MyBluetoothService
        /****end code bluetooth****/
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            //Log.d("TAG_TEST", action)
            Log.d("ServiceStart_","avant when action")
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    Log.d(TAG_UNPAIRED, "$deviceName  and $deviceHardwareAddress")
                    //if(deviceName != null){
                    listBluetooth.add(BluetoothDeviceContent(device.toString(),deviceName.toString(),false))
                    bluetoothViewModel.setListUnpairedDevices(listBluetooth)
                    //}
                }
                "MESSAGE_FROM_SERVER" -> {
                    val Texte = intent.getStringExtra("MESSAGE_SERVEUR_TO_CLIENT")
                    /**
                     * TODO
                     * à organiser + avec then
                     */
                    // les gardes fou
                    // test code
                    if(Texte!=""){
                        if(Texte!!.contains("Bien connecté au serveur")){
                            binding.joinServer.isClickable = false
                            Log.d("Clicke_element","Bien connecté au serveur")
                        }else if(Texte!!.contains("Serveur Bluetooth : serveur fermé .")){
                            binding.joinServer.isClickable = true
                            Log.d("Clicke_element","serveur fermé")
                        }else if(Texte!!.contains("Serveur Bluetooth fermé")){
                            binding.joinServer.isClickable = true
                            Log.d("Clicke_element","notification normal:"+Texte)
                        }
                        notificationManager?.sendNotificationUpdateDone(requireContext(),"Escape Game",Texte!!)
                    }
                }
                "EXCEPTION_SERVEUR_UNAVALAIBLE" -> {
                    val erreur = intent.getStringExtra("CODE_SERVEUR_UNAVALAIBLE")
                    if(erreur=="1"){
                        Toast.makeText(context, requireContext().getString(R.string.isnt_running), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO
        //  faire avec "and"
        if (requestCode == REQUEST_ENABLE_DISC){
            if(bluetoothAdapter.startDiscovery()){
                activity?.registerReceiver(receiver,filter)
            }
        }
    }

    // rechercher dans la liste ListDevice un nom de device scanné
    fun getMacAddress(ListDevice : MutableList<BluetoothDeviceContent>,name : String) : String{
        var serverAdress = ""
        for (device in ListDevice) {
            if (device.name == name) {
                serverAdress = device.mac
                break
            }
        }
        return serverAdress
    }
    override fun onDestroy() {
        super.onDestroy()
        /**
         * a bug raised when receiver not registred yet
         */
        // Don't forget to unregister the ACTION_FOUND receiver.
        activity?.unregisterReceiver(receiver)
    }

    // tester si GPS est activé
    private fun islocationEnabled() : Boolean{
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // tester si le service qui lance le thread server bluetooth toujours en état "running"
    fun isServiceBluetoothRunning(serviceClass: Class<*>): Boolean {
        val manager = activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    /******** end code bluetooth *********/
}