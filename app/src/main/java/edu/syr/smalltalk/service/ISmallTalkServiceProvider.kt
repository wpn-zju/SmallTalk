package edu.syr.smalltalk.service

interface ISmallTalkServiceProvider {
    fun hasService(): Boolean
    fun getService(): ISmallTalkService?
}