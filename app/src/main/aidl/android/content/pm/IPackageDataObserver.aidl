/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\yun\\工作\\dsk\\Android应用源码\\手机安全卫士工程源代码\\mobilesafe\\src\\android\\content\\pm\\IPackageStatsObserver.aidl
 */
package android.content.pm;

/**
 * API for package data change related callbacks from the Package Manager.
 * Some usage scenarios include deletion of cache directory, generate
 * statistics related to code, data, cache usage(TODO)
 * {@hide}
 */

oneway interface IPackageDataObserver{
void onRemoveCompleted(in String packageName,boolean succeeded);}
