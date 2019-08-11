package nl.looplan.batmate.tools;

/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.util.Log;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 * <p>
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 * <p>
 * Note that only one observer is going to be notified of changes.
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private MediatorLiveData<T> liveDataToObserve;
    private final MutableLiveData<Boolean> mPending = new MutableLiveData<>();

    public SingleLiveEvent() {
        liveDataToObserve = new MediatorLiveData<>();
        liveDataToObserve.addSource(this, new Observer<T>() {
            @Override
            public void onChanged(T currentValue) {
                liveDataToObserve.postValue(currentValue);
                mPending.postValue(null);
            }
        });
    }

    private boolean isPending() {
        return mPending.getValue() != null;
    }

    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        liveDataToObserve.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if(isPending()) {
                    observer.onChanged(t);
                }
            }
        });
    }

    @MainThread
    public void setValue(T value) {
        mPending.setValue(true);
        super.setValue(value);
    }

    @MainThread
    public void call(){
        setValue(null);
    }
}

