package com.example.SampleA3LMessagingApp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.amazon.A3L.messaging.A3LMessaging;
import com.example.SampleA3LMessagingApp.databinding.TbmFragmentBinding;
import com.example.SampleA3LMessagingApp.pushNotification.PushNotifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class TBMFragment extends Fragment {
    private TbmFragmentBinding binding;
    private EditText editTextTopic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = TbmFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        editTextTopic = rootView.findViewById(R.id.topic);

        binding.subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = editTextTopic.getText().toString();
                Log.d("TopicName-Subscribe", topic);
                A3LMessaging.subscribeToTopic(topic).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Subscribed";
                                if (!task.isSuccessful()) {
                                    msg = "Subscribe failed" + task.getException();
                                }
                                Log.d("TBMA3L", msg);
                            }
                        }
                );
            }
        });

        binding.unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = editTextTopic.getText().toString();
                Log.d("TopicName-Unsubscribe", topic);
                A3LMessaging.unsubscribeFromTopic(topic).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = "Unsubscribed";
                                if (!task.isSuccessful()) {
                                    msg = "Unsubscribe failed";
                                }
                                Log.d("TBMA3L", msg);
                            }
                        }
                );
            }
        });

        binding.sendPushToTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = editTextTopic.getText().toString();
                Log.d("TopicName-Send", topic);
                PushNotifier.sendMessageToTopic(rootView, requireActivity().getApplicationContext(), topic);
                Toast.makeText(view.getContext(),"Check Notification Tray For Notification",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TBMFragment.this)
                        .navigate(R.id.action_TBMFragment_to_GBMFragment);
            }
        });

        binding.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TBMFragment.this)
                        .navigate(R.id.action_TBMFragment_to_TargetMessagingFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
