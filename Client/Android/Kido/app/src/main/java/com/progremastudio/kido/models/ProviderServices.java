/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.models;

import android.content.Context;

public interface ProviderServices {
    public void insert(Context context);

    public void delete(Context context);

    public void edit(Context context);

    public void httpPost(Context context);
}
