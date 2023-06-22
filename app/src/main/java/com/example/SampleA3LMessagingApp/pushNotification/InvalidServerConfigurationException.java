package com.example.SampleA3LMessagingApp.pushNotification;

    public class InvalidServerConfigurationException extends RuntimeException {
        public InvalidServerConfigurationException(){
            super();
        }
        public InvalidServerConfigurationException(String message)
        {
            super(message);
        }
        public InvalidServerConfigurationException(String message, Throwable throwable){
            super(message, throwable);
        }
    }
