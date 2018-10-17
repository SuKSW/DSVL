package com.dsvl.flood.service.impl;

import com.dsvl.flood.UdpHelper;
import com.dsvl.flood.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.StringJoiner;

import static com.dsvl.flood.Constants.Protocol.REG;
import static com.dsvl.flood.UdpHelper.UDP_PORT;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Override
    public Boolean register(InetAddress bootstrapAddress, InetAddress nodeAddress, Integer bootstrapServerPort, Integer nodePort, String username) {

        int length = nodeAddress.getHostAddress().length() + UDP_PORT.toString().length() + username.length() + 7;

        StringJoiner msgBuilder = new StringJoiner(" ");
        msgBuilder.add(String.format("%04d", length))
                .add(REG.name())
                .add(nodeAddress.getHostAddress())
                .add(UDP_PORT.toString())
                .add(username);

        if (!UdpHelper.sendMessage(msgBuilder.toString(), bootstrapAddress, bootstrapServerPort)) {
            logger.warn("Registering with the bootstrap server failed");
            return false;
        }

        if (UdpHelper.receiveMessage() != null) {
            logger.info("Successfully registered with the bootstrap server");
            return true;
        }

        return false;
    }

}