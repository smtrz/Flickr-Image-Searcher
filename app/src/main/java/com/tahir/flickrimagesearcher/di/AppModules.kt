package com.tahir.flickrimagesearcher.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [NetworkModule::class])
@ComponentScan("com.tahir.flickrimagesearcher")
class AppModules