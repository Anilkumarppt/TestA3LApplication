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
import com.example.SampleA3LMessagingApp.databinding.GbmFragmentBinding;
import com.example.SampleA3LMessagingApp.pushNotification.PushNotifier;

public class GBMFragment extends Fragment {
    private GbmFragmentBinding binding;
    private EditText groupName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = GbmFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        groupName = rootView.findViewById(R.id.group);

        binding.createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group = groupName.getText().toString();
                Log.d("GroupName-Send", group);
                PushNotifier.createGroup(requireActivity().getApplicationContext(), group);
            }
        });

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group = groupName.getText().toString();
                Log.d("GroupName-Send", group);
                PushNotifier.sendMessageToGroup(requireActivity().getApplicationContext(), group);
            }
        });

        binding.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String group = groupName.getText().toString();
                Log.d("GroupName-Send", group);
                PushNotifier.sendMessageToGroup(requireActivity().getApplicationContext(), group);
            }
        });

        binding.sendToGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String group = groupName.getText().toString();
                Log.d("GroupName-Send", group);
                PushNotifier.sendMessageToGroup(requireActivity().getApplicationContext(), group);
                Toast.makeText(view.getContext(),"Check Notification Tray For Notification",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(GBMFragment.this)
                        .navigate(R.id.action_GBMFragment_to_TBMFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
