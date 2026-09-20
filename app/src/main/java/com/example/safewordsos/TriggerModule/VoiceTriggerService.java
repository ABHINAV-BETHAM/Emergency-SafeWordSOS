package com.example.safewordsos.TriggerModule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class VoiceTriggerService {
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private final Context context;
    private final TriggerCallback callback;
    private final String safeWord;
    private boolean isListening = false;

    public interface TriggerCallback {
        void onTriggered(String source);
    }

    public VoiceTriggerService(Context context, String safeWord, TriggerCallback callback) {
        this.context = context;
        this.safeWord = safeWord.toLowerCase();
        this.callback = callback;
        initSpeechRecognizer();
    }

    private void initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override public void onReadyForSpeech(Bundle params) {}
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {
                    if (isListening) startListening();
                }
                @Override public void onError(int error) {
                    if (isListening) startListening();
                }

                @Override
                public void onResults(Bundle results) {
                    check(results);
                    if (isListening) startListening();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    check(partialResults);
                }

                @Override public void onEvent(int eventType, Bundle params) {}
            });
        }
    }

    private void check(Bundle results) {
        if (results != null) {
            java.util.ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null) {
                for (String match : matches) {
                    if (match.toLowerCase().contains(safeWord)) {
                        isListening = false; // STOP LOOPING immediately
                        callback.onTriggered("Voice");
                        break;
                    }
                }
            }
        }
    }

    public void startListening() {
        if (speechRecognizer != null) {
            isListening = true;
            try { speechRecognizer.startListening(speechIntent); } catch (Exception ignored) {}
        }
    }

    public void stop() {
        isListening = false;
        if (speechRecognizer != null) {
            speechRecognizer.cancel(); // Force release the microphone
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}