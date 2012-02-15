package com.voxelbox.xtiming;

import java.io.*;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet3Chat;

public class XTModsChatPacket extends Packet3Chat {
	public XTModsChatPacket(String message) {
        this.message = message;
	}

	@Override
    public void a(DataInputStream datainputstream) throws IOException {
        message = a(datainputstream, 500);
    }

	@Override
    public void a(DataOutputStream dataoutputstream) throws IOException {
        a(message, dataoutputstream);
    }

	@Override
    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

	@Override
    public int a()  {
        return message.length();
    }
}
