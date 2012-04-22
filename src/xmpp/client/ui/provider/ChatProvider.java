package xmpp.client.ui.provider;

import xmpp.client.Constants;
import xmpp.client.service.chat.ChatMessage;
import xmpp.client.service.chat.ChatSession;
import xmpp.client.service.chat.multi.MultiUserChatSession;
import xmpp.client.service.handlers.SimpleMessageHandler;
import xmpp.client.service.handlers.SimpleMessageHandlerClient;
import xmpp.client.service.user.UserList;
import xmpp.client.service.user.contact.Contact;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class ChatProvider implements SimpleMessageHandlerClient, Constants {
	private static final String TAG = ChatProvider.class.getName();

	private ChatSession mChatSession;
	private Contact mMeContact;
	private final ChatProviderListener mListener;

	public ChatProvider(Contact meContact, ChatSession chatSession,
			ChatProviderListener listener, SimpleMessageHandler messageHandler) {
		mChatSession = chatSession;
		mMeContact = meContact;
		mListener = listener;
		messageHandler.addClient(this);
	}

	public void addMessage(ChatMessage message) {
		mChatSession.addMessage(message);

	}

	public Contact getMeContact() {
		return mMeContact;
	}

	public ChatMessage getMessage(int position) {
		return mChatSession.getMessageList().get(position);
	}

	public UserList getUsers() {
		if (mChatSession.isMUC()
				&& (mChatSession instanceof MultiUserChatSession)) {
			return ((MultiUserChatSession) mChatSession).getUsers();
		}
		return null;
	}

	@Override
	public void handleMessage(Message msg) {
		try {
			final Bundle b = msg.getData();
			switch (msg.what) {
			case SIG_MESSAGE_SENT:
			case SIG_MESSAGE_GOT:
				b.setClassLoader(ChatMessage.class.getClassLoader());
				final ChatMessage message = b.getParcelable(FIELD_MESSAGE);
				addMessage(message);
				if (mListener.isReady()) {
					mListener.chatProviderChanged(this);
				}
				break;
			}
		} catch (final Exception e) {
			Log.e(TAG, "handleMessage", e);
		}
	}

	public boolean isMUC() {
		return mChatSession.isMUC();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	public void setMeContact(Contact contact) {
		mMeContact = contact;
	}

	public void setSession(ChatSession mSession) {
		mChatSession = mSession;
	}

	public int size() {
		return mChatSession.getMessageList().size();
	}

}
