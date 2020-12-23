package com.example.mainactivity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    EditText username,email,password;
    Button signup,back;

    private FirebaseAuth mAuth;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.emailID);
        password = view.findViewById(R.id.Password);
        signup = view.findViewById(R.id.signupButton);
        back = view.findViewById(R.id.backButton);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameStr = username.getText().toString();
                final String emailIDStr = email.getText().toString();
                final String passwordStr = password.getText().toString();

                mAuth = FirebaseAuth.getInstance();
                if(usernameStr.isEmpty()){
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.name_hint));
                }else if (emailIDStr.isEmpty() && !emailIDStr.matches("^[A-Za-z0-9+_.-]+@[a-zA-Z0-9]+\\.[a-zA-Z]+$")) {
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.email_hint));
                } else if (passwordStr.isEmpty() && (passwordStr.length() < 6)) {
                    AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error), getResources().getString(R.string.password_hint));
                } else {
                    mAuth.createUserWithEmailAndPassword(emailIDStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> map = new HashMap<>();
                                map.put("username",usernameStr);
                                db.collection("users").document(user.getUid()).set(map);
                                signUpFragmentInterface.callMainActivity();
                            }else {
                                AlertUtils.showOKDialog(getContext(), getResources().getString(R.string.error),task.getException().getMessage());
                            }
                        }
                    });
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpFragmentInterface.goBackToLoginFragment();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        signUpFragmentInterface = (SignUpFragmentInterface) context;
    }

    SignUpFragmentInterface signUpFragmentInterface;

    public interface SignUpFragmentInterface{
        void goBackToLoginFragment();
        void callMainActivity();
    }
}