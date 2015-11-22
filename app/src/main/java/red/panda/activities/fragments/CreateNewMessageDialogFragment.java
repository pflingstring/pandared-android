package red.panda.activities.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response.*;
import com.android.volley.VolleyError;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import red.panda.R;
import red.panda.requests.UserRequest;
import red.panda.utils.JsonUtils;
import red.panda.utils.SocketUtils;
import red.panda.utils.misc.RequestQueueSingleton;

public class CreateNewMessageDialogFragment extends DialogFragment
{
    public CreateNewMessageDialogFragment() {}

    private Socket socket = SocketUtils.init();
    private EditText usernameEditText;
    private EditText messageEditText;
    private String userId;
    private String message;
    private View sendNewMessageView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        sendNewMessageView = inflater.inflate(R.layout.fragment_create_new_message, null);

        builder
            .setView(sendNewMessageView)
            .setPositiveButton("Send", onSendButton())
            .setNegativeButton("Cancel", onCancelButton())
            .setMessage("Send a new message");

        socket.off();
        return builder.create();
    }

    DialogInterface.OnClickListener onSendButton()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                usernameEditText = (EditText) sendNewMessageView.findViewById(R.id.toUser);
                messageEditText = (EditText) sendNewMessageView.findViewById(R.id.message);
                final String username = usernameEditText.getText().toString();
                message = messageEditText.getText().toString();

                RequestQueueSingleton.addToQueue(new UserRequest(username, onResponse, onError), getActivity());
            }
        };
    }

    Listener<String> onResponse = new Listener<String>()
    {
        @Override
        public void onResponse(String response)
        {
            try
            {
                JSONObject jsonResponse = new JSONObject(response).getJSONObject("data");
                JSONObject jsonMessage = new JSONObject();
                userId = JsonUtils.getFieldFromJSON(jsonResponse, "id");
                jsonMessage.put("msg", message);
                jsonMessage.put("userId", userId);

                socket.emit("conversation:post", jsonMessage);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    };

    ErrorListener onError = new ErrorListener()
    {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            //TODO: handle on error response
        }
    };

    DialogInterface.OnClickListener onCancelButton()
    {
        return new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                CreateNewMessageDialogFragment.this.getDialog().cancel();
            }
        };
    }

}
