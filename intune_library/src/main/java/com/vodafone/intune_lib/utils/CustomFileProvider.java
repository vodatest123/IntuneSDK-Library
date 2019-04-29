/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.vodafone.intune_lib.utils;

import android.support.v4.content.FileProvider;

/**
 * This FileProvider allows the app to export files to other apps.
 *
 * Will automatically be blocked by MAM if necessary.
 */
public class CustomFileProvider extends FileProvider { }
