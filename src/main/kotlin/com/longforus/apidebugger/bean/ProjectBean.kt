package com.longforus.apidebugger.bean

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

/**
 * Created by XQ Yang on 8/30/2018  5:40 PM.
 * Description :
 */
@Entity
data class ProjectBean(@Id var id: Long = 0,@Index val name:String,val baseUrlList:List<String> = listOf(),var apiList:List<ApiBean> = listOf())