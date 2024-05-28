package org.unibl.etf.mr.planact.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.unibl.etf.mr.planact.MainActivity;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.databinding.FragmentDetailsBinding;
import org.unibl.etf.mr.planact.databinding.FragmentNotificationsBinding;
import org.unibl.etf.mr.planact.databinding.FragmentSettingsBinding;
import org.unibl.etf.mr.planact.ui.notifications.NotificationsViewModel;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    FrameLayout loadingLayout;
    FrameLayout mainLayout;
    private FragmentSettingsBinding binding;
    View root;

    public SettingsFragment() {

    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);



        ListPreference listPreference = findPreference("language_preference");
//        getActivity().getsha

        if (listPreference != null)
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = listPreference.findIndexOfValue(newValue.toString());
                    CharSequence entry = listPreference.getEntries()[index];
                    changeLanguage(entry.toString());
                    listPreference.setSummary(entry);
                    return true;
                }
            });



    }

    public void changeLanguage(String newLang){
        // uzimamo trenutni jezik iz SharedPreferences-a
        SharedPreferences shPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String lang = shPreferences.getString(MainActivity.SELECTED_LANGUAGE, Locale.getDefault().getLanguage());
        if(newLang.equals(lang)){
            return;
        }
        // promijenimo jezik
        if(newLang.equals("English"))
            setLocale(requireContext(), "en");
        else if(newLang.equals("Deutsch"))
            setLocale(requireContext(), "de");
        else if(newLang.equals("Srpski"))
            setLocale(requireContext(), "sr");
        // restartujemo activity
        requireActivity().recreate();
    }

    public Context setLocale(Context context, String language) {
        // sacuvamo novi jezik u SharedPreferences
        SharedPreferences shPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = shPreferences.edit();
        editor.putString(MainActivity.SELECTED_LANGUAGE, language);
        editor.apply();

        // sacuvamo promjene u konfiguraciji aplikacije
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
        return context;
    }




    @Override
    public void onResume() {
        super.onResume();
//        loadingLayout.setVisibility(View.INVISIBLE);
//        mainLayout.setVisibility(View.VISIBLE);

        ListPreference languagePreference = findPreference("language_preference");
        // Retrieve the selected value from SharedPreferences
        String selectedValue = languagePreference.getValue();

        // Find the index of the selected value in the entries array
        int selectedIndex = languagePreference.findIndexOfValue(selectedValue);

        // Update the summary text with the corresponding entry
        if (selectedIndex != -1) {
            CharSequence selectedEntry = languagePreference.getEntries()[selectedIndex];
            languagePreference.setSummary(selectedEntry);
        }

    }


}