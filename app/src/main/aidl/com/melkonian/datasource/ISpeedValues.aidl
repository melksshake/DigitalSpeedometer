package com.melkonian.datasource;

import com.melkonian.datasource.IOnSpeedChangedListener;

interface ISpeedValues {
    void registerCallback(IOnSpeedChangedListener listener);
}