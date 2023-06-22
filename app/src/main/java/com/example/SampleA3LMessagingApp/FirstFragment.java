package com.example.SampleA3LMessagingApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.SampleA3LMessagingApp.databinding.FragmentFirstBinding;
import com.example.SampleA3LMessagingApp.pushNotification.PushNotifier;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        binding.sendPushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushNotifier.sendPushNotification(rootView, requireActivity().getApplicationContext());
                Toast.makeText(view.getContext(),"Check Notification Tray For Notification",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_TargetMessagingFragment_to_TBMFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}