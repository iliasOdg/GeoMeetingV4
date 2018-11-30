package project.miage.geomeetingv4;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SendRequestFragment extends Fragment {

    private static Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    private static String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private ArrayList<Contact> contactsList;
    private MyAdapter adapter;
    private String DateRdv;
    List<String> intitals = new ArrayList<>();

    public static SendRequestFragment newInstance() {
        return new SendRequestFragment();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        return inflater.inflate(R.layout.sendrequestlayout_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        // TODO Add your menu entries here
        inflater.inflate(R.menu.sendrequest_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.i("SendRequestFragment", "clique button search");
                break;

            case R.id.action_addContact:
                Log.i("SendRequestFragment", "clique button addcontact");
                break;
        }
        return true;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        adapter = new MyAdapter(getContext(), loadContacts());



        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //getActivity().getActionBar().setTitle(R.string.destinataires);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.contactListView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    public ArrayList<Contact> loadContacts() {

        Cursor phones;
        phones = getContext().getContentResolver().query(CONTENT_URI, new String[]{NAME, NUMBER},null,null, null);
        Set<Contact> Contacts = new HashSet<>();

        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(NUMBER));

            Contacts.add(new Contact(name,phoneNumber));
            intitals.add(name.charAt(0)+""+name.charAt(1));

        }
        Log.i("","List size ="+Contacts.size());
        contactsList = new ArrayList<Contact>(Contacts);

        Collections.sort(contactsList, new CustomComparator());
        phones.close();
        return contactsList;
    }

    public class CustomComparator implements Comparator<Contact> {
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getNom().compareTo(o2.getNom());
        }
    }
}
