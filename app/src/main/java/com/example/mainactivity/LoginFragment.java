package com.example.mainactivity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    final String TAG = "TAG";

    EditText emailAddressET, passwordET;
    Button login, signup;


    FirebaseAuth mAuth;
    public LoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");
        emailAddressET = view.findViewById(R.id.EmailAddressEditText);
        passwordET = view.findViewById(R.id.PasswordEditText);
        login = view.findViewById(R.id.LogIn);
        signup = view.findViewById(R.id.SignUp);
        passwordET.setInputType(InputType.TYPE_CLASS_TEXT);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailAddress = emailAddressET.getText().toString(),password = passwordET.getText().toString();
                if(emailAddress.isEmpty() && !emailAddress.matches("^[A-Za-z0-9+_.-]+@[a-zA-Z0-9]+\\.[a-zA-Z]+$")){
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.email_hint));
                }else if(password.isEmpty()){
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.password_hint));
                }else{
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(emailAddress,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d(TAG, "onSuccess: ");
                            mListener.startMainActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                            AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.invalidemail));
                        }
                    });
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startSignUpFragment();
            }
        });

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (loginScreenInterface)context;
    }

    loginScreenInterface mListener;

    public interface loginScreenInterface{
        void startMainActivity();
        void startSignUpFragment();
    }
}