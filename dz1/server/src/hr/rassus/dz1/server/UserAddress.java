package hr.rassus.dz1.server;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: msantl
 * Date: 10/16/13
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserAddress implements Serializable{
    private int _port;
    private String _address;

    public UserAddress(int _port, String _address) {
        this._port = _port;
        this._address = _address;
    }

    public UserAddress(UserAddress a) {
        this._port = a.get_port();
        this._address = a.get_address();
    }

    public int get_port() {
        return _port;
    }

    public String get_address() {
        return _address;
    }

    public void set_port(int _port) {
        this._port = _port;
    }

    public UserAddress(String _address) {
        this._address = _address;
    }

    public int hashCode() {
        return _port ^ _address.hashCode();
    }

    public boolean equals(UserAddress rhs) {
        return (_port == rhs.get_port() && _address.equals(rhs.get_address()));
    }

}

