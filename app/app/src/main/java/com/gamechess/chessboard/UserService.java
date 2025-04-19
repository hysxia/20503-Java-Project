package com.gamechess.chessboard;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private UserApi userApi;

    public UserService(){
        userApi = ServerConnection.getUserApi();
    }
    //function for loggin in a user
    public void login(String username, String password, final UserService.UserCallback<User> callback) {
        Call<User> call = userApi.login(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.errorBody() != null) {
                    try {
                        System.out.println("Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage;
                    if (!response.isSuccessful()) {
                        if (response.code() == 401) {
                           //massage that explaind the error
                            errorMessage = "Invalid username or password.";
                        } else {
                            //automatic massage for all other error
                            errorMessage = "Response isn't successful. Code: " + response.code();
                        }
                    } else {
                        //in case body is null
                        errorMessage = "Response body is null.";
                    }
                    callback.onFailure(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //function for registering a new user
    public void register(String username, String email, String password, final UserService.UserCallback<User> callback) {
        Call<User> call = userApi.register(username, email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.errorBody() != null) {
                    try {
                        System.out.println("Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Register failed.";
                    if (!response.isSuccessful()) {
                        if (response.code() == 409) {
                            errorMessage = "Email is already registered.";
                        } else if (response.code() == 400) {
                            errorMessage = "Invalid email format.";
                        } else {
                            errorMessage = "Error " + response.code() + ": Registration failed.";
                        }
                    } else if (response.body() == null) {
                        errorMessage = "Response body is null.";
                    }
                    callback.onFailure(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //function for retrieving a user
    public void getUser(String username, final UserService.UserCallback<User> callback) {
        Call<User> call = userApi.getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.errorBody() != null) {
                    try {
                        System.out.println("Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Getting a username failed.";
                    if (!response.isSuccessful()) {
                        errorMessage = "Response isn't successful. Code: " + response.code();
                    } else if (response.body() == null) {
                        errorMessage = "Response body is null.";
                    }
                    callback.onFailure(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
//getting the wins of the user
    public void getWins(String username, final UserCallback<Integer> callback) {
        Call<Integer> call = userApi.getWins(username);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {


                if (response.errorBody() != null) {
                    try {
                        System.out.println("Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Getting a username failed.";
                    if (!response.isSuccessful()) {
                        errorMessage = "Response isn't successful. Code: " + response.code();
                    } else if (response.body() == null) {
                        errorMessage = "Response body is null.";
                    }
                    callback.onFailure(errorMessage);
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //getting the draws of the user
    public void getDraws(String username, final UserCallback<Integer> callback) {
        Call<Integer> call = userApi.getDraws(username);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.errorBody() != null) {
                    try {
                        System.out.println("Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Getting a username failed.";
                    if (!response.isSuccessful()) {
                        errorMessage = "Response isn't successful. Code: " + response.code();
                    } else if (response.body() == null) {
                        errorMessage = "Response body is null.";
                    }
                    callback.onFailure(errorMessage);
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //getting the losses of the user
    public void getLoses(String username, final UserCallback<Integer> callback) {
        Call<Integer> call = userApi.getLosses(username);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {


                if (response.errorBody() != null) {
                    try {
                        System.out.println("Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Getting a losses number failed.";
                    if (!response.isSuccessful()) {
                        errorMessage = "Response isn't successful. Code: " + response.code();
                    } else if (response.body() == null) {
                        errorMessage = "Response body is null.";
                    }
                    callback.onFailure(errorMessage);
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }


    //Callback interface for handling API responses.
    public interface UserCallback<T> {
        void onSuccess(T result);
        void onFailure(String errorMessage);
    }

}
