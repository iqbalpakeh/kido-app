/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface InterfaceHistoryFragment {

    void prepareFragment(LayoutInflater inflater, ViewGroup container);

    void attachView();

    void prepareListView();

    void prepareLoaderManager();

}
