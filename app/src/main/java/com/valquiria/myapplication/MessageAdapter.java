package com.valquiria.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ChatMessage>{

    private String TAG = "MessageAdapter";
    private List<ChatMessage> dataSet;
    private List<String> friendUid;
    private List<Usuarios> friends;

    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtMessage;
        ImageView imgPhoto;
    }

    public MessageAdapter(List<ChatMessage> data, List<String> friendUid, List<Usuarios> friends, Context context) {
        super(context, R.layout.list_messages, data);
        this.dataSet = data;
        this.friendUid = friendUid;
        this.mContext = context;
        this.friends = friends;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage dataModel = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_messages, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.txt_adapter_name);
            viewHolder.txtMessage = convertView.findViewById(R.id.txt_adapter_message);
            viewHolder.imgPhoto = convertView.findViewById(R.id.img_adapterlist_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // TODO: AGREGAR FOTO ACA
        Usuarios actual = findUid(friendUid.get(position));
        if(actual != null){
            if(actual.getNombre().isEmpty()){
                viewHolder.txtName.setText(actual.getCorreo());
            }else{
                viewHolder.txtName.setText(actual.getNombre());
            }
            viewHolder.txtMessage.setText(dataModel.getMessageText());
        }
        return convertView;
    }

    private Usuarios findUid(String uid){
        for(Usuarios curr : friends){
            if(curr.getId().equals(uid)) return curr;
        }
        return null;
    }
}
