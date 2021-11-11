package fr.mastergime.meghasli.escapegame.services

import android.content.Context
import android.media.MediaPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.components.SingletonComponent
import fr.mastergime.meghasli.escapegame.R
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object MediaPlayerSerivce {

    @Provides
    @ActivityScoped
    fun providesMediaPlayer(
        @ApplicationContext context: Context
    ) = MediaPlayer.create(context, R.raw.music)

}