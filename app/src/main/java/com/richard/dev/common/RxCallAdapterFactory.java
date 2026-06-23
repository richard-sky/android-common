package com.richard.dev.common;

import androidx.annotation.NonNull;

import com.richard.library.net.http.model.BasicResponse;
import com.richard.library.simplerx.XObservable;
import com.richard.library.simplerx.XObservableOnSubscribe;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * @author: admin-richard
 * @createDate: 2022/8/23 15:34
 * @version: 1.0
 * @description: 描述
 */
public class RxCallAdapterFactory extends CallAdapter.Factory {

    public static CallAdapter.Factory create() {
        return new RxCallAdapterFactory();
    }

    @Override
    public RxCallAdapter get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != XObservable.class) {
            return null;
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType != BasicResponse.class) {
            throw new IllegalArgumentException("type must be a resource");
        }
        if (! (observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("resource must be parameterized");
        }
        Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        return new RxCallAdapter(bodyType);
    }

    private static class RxCallAdapter implements CallAdapter<ResponseBody, XObservable<ResponseBody>> {

        private final Type responseType;

        public RxCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @NonNull
        @Override
        public Type responseType() {
            return responseType;
        }

        @NonNull
        @Override
        public XObservable<ResponseBody> adapt(@NonNull Call<ResponseBody> call) {
            return XObservable
                    .create(new XObservableOnSubscribe<ResponseBody>() {
                        @Override
                        public ResponseBody run() throws Throwable {
                            return call.execute().raw().body();
                        }
                    });
        }
    }
}
