/*
 * (C) Copyright 2015 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.jaea.onlinevideotutorials;

import com.google.common.reflect.TypeToken;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.OnIceCandidateEvent;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.List;

/**
 * Online Video Tutorials handler 
 * 
 * @author Juan Antonio Echeverrías Aranda (juanan.echeve@gmail.com)
*/
public class OnlineVideoTutorialsHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(OnlineVideoTutorialsHandler.class);
    private static final Gson gson = new GsonBuilder().create();
    private UserSession me = null;
    
    @Autowired
	private RoomManager roomManager;

	@Autowired
	private UserSessionsRegistry users;
    
    @Autowired
    private KurentoClient kurento;
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);

	log.debug("Incoming message: {}", jsonMessage);

	switch (jsonMessage.get("id").getAsString()) {
                    
        case "login":
            Info.receiveMsg("login");
            this.login(session, jsonMessage);
            break;
            
        case "waitingRoom":
            Info.receiveMsg("waitingRoom");
            this.waitingRoom(session, jsonMessage);
            break;
            
        case "joinRoom":
            Info.receiveMsg("joinRoom");
            this.joinRoom(session, jsonMessage);
            break;
            
        case "onIceCandidate":
            Info.receiveMsg("onIceCandidate");
            this.iceCandidate(session, jsonMessage);
            break;    
            
        case "receiveVideoFrom":
            Info.receiveMsg("receiveVideoFrom");
            this.receiveVideoFrom(session, jsonMessage);
            break;
		
        case "exitRoom":
            Info.receiveMsg("exitRoom");
            this.exitRoom(session, jsonMessage);
            break;
        
        default:
            break;
		
	}
    }
    
    private void login(final WebSocketSession session, JsonObject jsonMessage){
        log.info("<- login - Id: {}", session.getId());
        log.info("<- message: {}", jsonMessage.toString());
        
        String userName = jsonMessage.get("userName").getAsString();
        String password = jsonMessage.get("password").getAsString();
        
        UserSession newUser = validateUSer(userName, password, session);
        
        JsonObject jsonAnswer = new JsonObject();
        jsonAnswer.addProperty("id","login");
        jsonAnswer.addProperty("validUser",newUser!=null);
        jsonAnswer.addProperty("name",newUser.getName());
        jsonAnswer.addProperty("userName",newUser.getUserName());
        jsonAnswer.addProperty("userType",newUser.getUserType());
        
       
        if (newUser == null ){
            
            log.info("Is not a valid user");
            
        }    
        else{ // A valid user
            
            this.users.addUser(newUser);
                
            // A tutor create a new room
            if (newUser.getUserType().equals(UserSession.TUTOR_TYPE)){
                
                log.info("The user is a tutor named {}", newUser.getUserName());
                
                this.roomManager.createRoom(newUser.getUserName());
                jsonAnswer.addProperty("roomName",newUser.getUserName());
                this.makeKnowThereIsANewRoom(newUser.getUserName());
                
             }
            
            else { // The user is an student
                
                log.info("The user is a student");
                
                users.addIncomingParticipant(newUser);
                
            }
            
        }     
         
        SendMessage.toClient(jsonAnswer, session);
        log.info("/login - the message has been sent");
    }
    
    
    /**
     * 
     * @param userName The userName of the user.
     * @param password The password of the user.
     * @return UserSession/null A new UserSession or 'null' if the user doesn't exist in the data base. 
     */
    private UserSession validateUSer(String userName, String password, WebSocketSession session ){
        UserSession user = null;
        
        // Here should be a query at the data base of the university
        
        String name = userName;
        
        // This is a provisional implementation
        if (userName.toLowerCase().contains(UserSession.TUTOR_TYPE)){
            user = new UserSession(session, userName, UserSession.TUTOR_TYPE, name);
            
        }
        else if (userName.toLowerCase().contains(UserSession.STUDENT_TYPE)){
            user = new UserSession(session, userName, UserSession.STUDENT_TYPE, name);
        }
        
        
        return user;
    }
    
    
    /**
     * 
     * @param roomName 
     */
    private void makeKnowThereIsANewRoom(String roomName){
        log.info(" * makeKnowThereIsANewRoom to...");
        
        JsonObject jsonAnswer = new JsonObject();
        jsonAnswer.addProperty("id", "thereIsANewRoom");
        jsonAnswer.addProperty("roomName", roomName);
        
        users.sendAMessageToIncomingParticipants(jsonAnswer);
        log.info(" /makeKnowThereIsANewRoom - the message has been sent");
     }
    
    
    private void waitingRoom (final WebSocketSession session, JsonObject jsonMessage){
        log.info("<- waitingRoom - Id: {}", session.getId());
        log.info("<- message: {}", jsonMessage.toString());
        
        JsonElement avaibleRoomsNames = gson.toJsonTree(this.roomManager.getAvaibleRoomsNames(), new TypeToken<List<String>>() {}.getType());
        JsonObject jsonAnswer = new JsonObject();
        jsonAnswer.addProperty("id","avaibleRooms");
        jsonAnswer.add("avaibleRoomsNames", avaibleRoomsNames);
        
        SendMessage.toClient(jsonAnswer, session);
        log.info("/waitingRoom - the message has been sent");
     
     }
    
    
    private void joinRoom (WebSocketSession session, JsonObject jsonMessage){
        log.info("<- joinRoom -> Id: {}", session.getId());
        log.info("<- message: {}", jsonMessage.toString());
        
        String name = jsonMessage.get("name").getAsString();
        String userName = jsonMessage.get("userName").getAsString();
        String roomName = jsonMessage.get("roomName").getAsString();
        String userType = jsonMessage.get("userType").getAsString();
        
        UserSession newParticipant;
        
       
        if (userType.equals(UserSession.STUDENT_TYPE)){
               
            newParticipant = users.removeIncomingParticipant(userName);
        }
        else{
             
            newParticipant = users.getUserBySessionId(session.getId());
        }
        
        log.info("userName: {}", userName);
        log.info("roomName: {}", roomName);
        
        this.roomManager.addParticipant(newParticipant, roomName);
        
       log.info("/joinRoom - the message has been sent");
    }
    
    
    private void iceCandidate(WebSocketSession session, JsonObject jsonMessage){
        log.info("<- iceCandidate -> Id: {}", session.getId());
        log.info("<- message: {}", jsonMessage.toString());
        
        JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();
        
        UserSession participant = users.getUserBySessionId(session.getId());
        
	if (participant != null) {
            IceCandidate cand = new IceCandidate(candidate.get("candidate").getAsString(),
                                        candidate.get("sdpMid").getAsString(),
					candidate.get("sdpMLineIndex").getAsInt());
            participant.addCandidate(cand, jsonMessage.get("userName").getAsString());
	}
        log.info("/ iceCandidate"); 
    }
    
    
    private void receiveVideoFrom(WebSocketSession session, JsonObject jsonMessage){
        log.info("<- receiveVideoFrom -> Id: {}", session.getId());
        log.info("<- message: {}", jsonMessage.toString());
        
        UserSession participant = users.getUserBySessionId(session.getId());
        final String userName = jsonMessage.get("userName").getAsString();
        final String roomName = jsonMessage.get("roomName").getAsString();
        final UserSession sender = this.roomManager.getParticipant(userName, roomName);
        final String sdpOffer = jsonMessage.get("sdpOffer").getAsString();
        participant.receivesGreetingsFrom(sender, sdpOffer);
            
        log.info("/ receiveVideoFrom");    
    }
    
    
    private void exitRoom (WebSocketSession session, JsonObject jsonMessage){
        log.info("<- exitRoom - Id: {}", session.getId());
        log.info("<- message: {}", jsonMessage.toString());
        
        String roomName = jsonMessage.get("roomName").getAsString();
        String userName = jsonMessage.get("userName").getAsString();
        String userType = jsonMessage.get("userType").getAsString();
        log.info("room: {}", roomName);
        
        UserSession user = roomManager.participantLeavesARoom(userName, roomName);
        
        if (userType.equals(UserSession.TUTOR_TYPE)){
            this.makeKnowThereIsAnAvaibleRoomLess(roomName);
        }
        else{
            users.addIncomingParticipant(user);
        }
        
        log.info("/exitRoom - it has finished");
        
    }
    
   
    
    /**
     * 
     * @param roomName 
     */
    private void makeKnowThereIsAnAvaibleRoomLess(String roomName){
        log.info("  * makeKnowThereIsARoomLess to...");
        
        JsonObject jsonAnswer = new JsonObject();
        jsonAnswer.addProperty("id", "thereIsAnAvaibleRoomLess");
        jsonAnswer.addProperty("roomName", roomName);
        
        users.sendAMessageToIncomingParticipants(jsonAnswer);
        
        log.info("  /makeKnowThereIsARoomLess - the message has been sent");
     }
    
    
	
}
