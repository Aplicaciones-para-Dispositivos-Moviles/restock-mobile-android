package com.uitopic.restockmobile.features.profiles.presentation.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object PresentationModule {
    // Este módulo está listo para proveer dependencias específicas
    // del ViewModel si las necesitas en el futuro
    // Por ahora, Hilt inyectará automáticamente el ProfileRepository
    // en el ProfileViewModel
}