package io.wireguard;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ConnectionInfo {

    /* FIXME: !!! protect private_key !!!! */
    public static class Interface implements Parcelable {
        private String mIp = null;
        private int mListenPort = 0;
        private String mPrivateKey = null;

        public Interface() {}

        protected Interface(Parcel in) {
            mIp = in.readString();
            mListenPort = in.readInt();
            mPrivateKey = in.readString();
        }

        public static final Creator<Interface> CREATOR = new Creator<Interface>() {
            @Override
            public Interface createFromParcel(Parcel in) {
                return new Interface(in);
            }

            @Override
            public Interface[] newArray(int size) {
                return new Interface[size];
            }
        };

        public void setInterfaceIp(String ip) { mIp = ip; }
        public void setListenPort(int port) { mListenPort = port; }
        public void setPrivateKey(String key) { mPrivateKey = key; }

        public String interfaceIp() { return mIp; }
        public int listenPort() { return mListenPort; }
        public String privateKey() { return mPrivateKey; }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(mIp);
            parcel.writeInt(mListenPort);
            parcel.writeString(mPrivateKey);
        }
    }

    public static class Peer implements Parcelable {

        public static class AllowedIp implements Parcelable {
            private String mIp = null;
            private int mCidr = 0;

            public AllowedIp(String ip, int cidr) {
                this.mIp = ip;
                this.mCidr = cidr;
            }

            protected AllowedIp(Parcel in) {
                mIp = in.readString();
                mCidr = in.readInt();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(mIp);
                dest.writeInt(mCidr);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<AllowedIp> CREATOR = new Creator<AllowedIp>() {
                @Override
                public AllowedIp createFromParcel(Parcel in) {
                    return new AllowedIp(in);
                }

                @Override
                public AllowedIp[] newArray(int size) {
                    return new AllowedIp[size];
                }
            };

            public void setIp(String ip) { mIp = ip; }
            public void setCidr(int cidr) { mCidr = cidr; }

            public String ip() { return mIp; }
            public int cidr() { return mCidr; }
        }

        private String mPublicKey = null;
        private String mEndpointIp = null;
        private int mEndpointPort = 0;
        private ArrayList<AllowedIp> mAllowedIps = new ArrayList<>();

        public Peer() {}

        protected Peer(Parcel in) {
            mPublicKey = in.readString();
            mEndpointIp = in.readString();
            mEndpointPort = in.readInt();
            in.readTypedList(mAllowedIps, AllowedIp.CREATOR);
        }

        public static final Creator<Peer> CREATOR = new Creator<Peer>() {
            @Override
            public Peer createFromParcel(Parcel in) {
                return new Peer(in);
            }

            @Override
            public Peer[] newArray(int size) {
                return new Peer[size];
            }
        };

        public void setPublicKey(String key) { mPublicKey = key; }
        public void setEndpointIp(String ip) { mEndpointIp = ip; }
        public void setEndpointPort(int port) { mEndpointPort = port; }
        public void addAllowedIp(String ip, int cidr) { mAllowedIps.add(new AllowedIp(ip, cidr)); }

        public String publicKey() { return mPublicKey; }
        public String endpointIp() { return mEndpointIp; }
        public int endpointPort() { return mEndpointPort; }
        public ArrayList<AllowedIp> allowedIps() { return mAllowedIps; }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(mPublicKey);
            parcel.writeString(mEndpointIp);
            parcel.writeInt(mEndpointPort);
            parcel.writeTypedList(mAllowedIps);
        }
    }

    private static final String prefix = ConnectionInfo.class.getPackage().getName();
    private static final String STORED_IINTERFACE = prefix + ".INTERFACE";
    private static final String STORED_PEERS = prefix + ".PEERS";
    private static final String STORED_TIME = prefix + ".TIME";

    private Interface mIface = null;
    private ArrayList<Peer> mPeers = new ArrayList<>();
    private long mConnectionTime = 0;

    public void setInterface(Interface iface) { mIface = iface; }
    public void setPeers(ArrayList<Peer> peers) { mPeers = peers; }
    public void addPeer(Peer peer) { mPeers.add(peer); }
    public void setConnectionTime(long time) { mConnectionTime = time; }

    public Interface iface() { return mIface; }
    public ArrayList<Peer> peers() { return mPeers; }
    public long connectionTime() { return mConnectionTime; }

    public static Bundle toBundle(ConnectionInfo info) {
        if (info == null) {
            return null;
        }

        Bundle ret = new Bundle();
        ret.putParcelable(STORED_IINTERFACE, info.iface());
        ret.putParcelableArrayList(STORED_PEERS, info.peers());
        ret.putLong(STORED_TIME, info.connectionTime());

        return ret;
    }

    public static ConnectionInfo fromBundle(Bundle extras) {
        if (extras == null) {
            return null;
        }

        ConnectionInfo ret = new ConnectionInfo();
        Interface iface = extras.getParcelable(STORED_IINTERFACE);
        ret.setInterface(iface);
        ArrayList<Peer> list = extras.getParcelableArrayList(STORED_PEERS);
        ret.setPeers(list);
        ret.setConnectionTime(extras.getLong(STORED_TIME));

        return ret;
    }
}

