/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jaea.onlinevideotutorials.handlers;


import org.jaea.onlinevideotutorials.Info;
import org.jaea.onlinevideotutorials.SendMessage;
import org.jaea.onlinevideotutorials.Hour;
import org.jaea.onlinevideotutorials.domain.Room;  //#####
import org.jaea.onlinevideotutorials.domain.ParticipantSession;
import org.jaea.onlinevideotutorials.domain.UserSession;
import org.jaea.onlinevideotutorials.domain.UserFile;
import org.jaea.onlinevideotutorials.domain.IceCandidatePayloadWSMessage;
import org.jaea.onlinevideotutorials.domain.MediaRoom;
import org.jaea.onlinevideotutorials.domain.OfferPayloadWSMessage;
import org.jaea.onlinevideotutorials.domain.WSMessage;
import org.jaea.onlinevideotutorials.managers.UserSessionsRegistry;
import org.jaea.onlinevideotutorials.managers.RoomsManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.kurento.client.KurentoClient;
import org.kurento.client.IceCandidate;


/**
 * 
 * 
 * @author Juan Antonio Echeverrías Aranda (juanan.echeve@gmail.com)
*/
public class RoomHandler extends TextMessageWebSocketHandler{
    
    /**
     * Valid  values of the message payload 'id' attribute which tell 
     * the handler that can handle the message.
     */
    private static final String ID_CLOSE_TAB = "closeTab";

    public static final String ID_JOIN_ROOM = "joinRoom";
    public static final String ID_RECEIVE_VIDEO_FROM = "receiveVideoFrom";
    public static final String ID_RECEIVE_ADDRESS = "receiveAddress";
    public static final String ID_EXIT_ROOM = "exitRoom";
    public static final String ID_GET_FILES = "getFiles";
    
    private String [] ids = {ID_JOIN_ROOM, ID_RECEIVE_VIDEO_FROM, ID_RECEIVE_ADDRESS, ID_GET_FILES, ID_EXIT_ROOM}; 
    /**
     * The id values to the messages send by websocket to the client
     */
    //public static final  String ID_THERE_IS_A_PARTICIPANT = "thereIsAParticipant";
    public static final  String ID_THESE_ARE_THE_PARTICIPANTS = "theseAreTheParticipants";
    public static final  String ID_THERE_IS_A_NEW_PARTICIPANT = "thereIsANewParticipant";
    public static final String ID_A_PARTICIPANT_HAS_LEFT_THE_ROOM = "aParticipantHasLeftTheRoom";
    public static final String ID_AVAILABLE_FILES = "availableFiles";
    
    private static final String PAYLOAD_ATTRIBUTE_USER_NAME = "userName";
    private static final String PAYLOAD_ATTRIBUTE_CANDIDATE = "address";
    private static final String PAYLOAD_ATTRIBUTE_OFFER = "offer";
    private static final String PAYLOAD_ATTRIBUTE_ROOM = "room";
    private static final String PAYLOAD_ATTRIBUTE_ID = "id";
   

    private final Logger log = LoggerFactory.getLogger(RoomHandler.class);
    private Gson gson = new GsonBuilder().create();
    
    @Autowired
    private RoomsManager roomsManager;
    
     @Autowired
    private UserSessionsRegistry usersRegistry;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;



    public RoomHandler(String attributeNameOfTheMessageId, String attributeNameOfTheMessagePayload){
        super(attributeNameOfTheMessageId, attributeNameOfTheMessagePayload);
    }

    public RoomHandler(String attributeNameOfTheMessageId, String attributeNameOfTheMessagePayload, GeneralHandler generalHandler){
        super(attributeNameOfTheMessageId, attributeNameOfTheMessagePayload);
        this.signIn(generalHandler);
    }


    private void signIn(GeneralHandler generalHandler){
        List<String> idsList = new ArrayList<>(Arrays.asList(this.ids));
        for(String id : idsList){
            generalHandler.attach(id, this);    
        }    
    }

    @Override
    public synchronized void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonObject jsonMessage = this.gson.fromJson(message.getPayload(), JsonObject.class);
        String id = this.getTextMessageId(message); 
        this.log.info("RoomHandler.handleTextMessage: {}", id);
        String userName = null; 
        Room room = null;
        String userType = null;       
        Type type = null;     
        IceCandidatePayloadWSMessage iceCandidatePayloadWSMessage = null;               
        OfferPayloadWSMessage offerPayloadWSMessage = null;               
        switch (id){
            case ID_JOIN_ROOM:
            try{
                this.log.info("TT");
                room = (Room) this.getTextMessagePayLoadDataAsObject(PAYLOAD_ATTRIBUTE_ROOM, new TypeToken<Room>() {}.getType(), message);
                this.log.info("TT - " + room.toString());
            }
            catch(Exception e){
                this.log.info("ERROR - {}: {}", ID_JOIN_ROOM, e.getMessage());
            }   
                this.joinRoom(session, room);
                break;
            case ID_RECEIVE_VIDEO_FROM:
                try{
                    offerPayloadWSMessage = (OfferPayloadWSMessage) this.getTextMessagePayLoadAsObject(new TypeToken<OfferPayloadWSMessage>() {}.getType(), message);
                }
                catch(Exception e){
                    this.log.info("ERROR - {}: {}",ID_RECEIVE_VIDEO_FROM, e.getMessage());
                }   
                this.receiveVideoFrom(session, offerPayloadWSMessage); 
                break;
            case ID_RECEIVE_ADDRESS:
                iceCandidatePayloadWSMessage = (IceCandidatePayloadWSMessage) this.getTextMessagePayLoadAsObject(new TypeToken<IceCandidatePayloadWSMessage>() {}.getType(), message);
                this.log.info("####### Receive Candidate:", iceCandidatePayloadWSMessage.getIceCandidate().getCandidate());
                try{
                    iceCandidatePayloadWSMessage = (IceCandidatePayloadWSMessage) this.getTextMessagePayLoadAsObject(new TypeToken<IceCandidatePayloadWSMessage>() {}.getType(), message);
                    
                }
                catch(Exception e){
                    this.log.info("ERROR - {}: {}",ID_RECEIVE_ADDRESS, e.getMessage());
                }   
                this.receiveAddress(session, iceCandidatePayloadWSMessage);
                break;
            case ID_GET_FILES :
                String roomId = this.getTextMessagePayLoadAsString(message);
                this.getRoomFiles(Long.parseLong(roomId), session);
                break;    
            case ID_EXIT_ROOM : case ID_CLOSE_TAB  :
                userName = this.getTextMessagePayLoadData(PAYLOAD_ATTRIBUTE_USER_NAME, message); 
                room = (Room) this.getTextMessagePayLoadDataAsObject(PAYLOAD_ATTRIBUTE_ROOM, new TypeToken<Room>() {}.getType(), message);
                this.exitRoom(session, userName, room);
                break;
            default:log.info("id {} doesn't found", id);
                throw new HandlerException("The handler does't know how handle the " + id + " message");   
        }
    }
    
    /**
    * An user has come into the waiting room.
    */
    private synchronized void joinRoom (WebSocketSession session, Room room){
        this.log.info("<- joinRoom -> id: {}", session.getId());
        
        /* //*
        String name = jsonMessage.get("name").getAsString();
        String userName = jsonMessage.get("userName").getAsString();
        Long roomId = jsonMessage.get("roomId").getAsLong();
        String userType = jsonMessage.get("userType").getAsString();
        */

       //* availableRoomsNames = gson.toJsonTree(this.roomsManager.getAvailableRoomsNames(), new TypeToken<List<String>>() {}.getType());
       Long roomId = null;
       try{
        roomId = room.getId(); 
        this.log.info("roomId: {}", roomId.toString());
       }
       catch(Exception e){
           this.log.info("ERROR - Room.getId(): {}", e.getMessage());
       }
       UserSession newParticipant = this.usersRegistry.getUserBySessionId(session.getId());
        
        this.roomsManager.addParticipant(newParticipant,roomId);
        this.log.info("########################## 2");
        this.makeKnowTheParticipantsOfRoom((ParticipantSession) newParticipant, roomId);
        this.log.info("########################## 3");
        this.makeKnowThereIsANewParticipant((ParticipantSession) newParticipant, roomId);
        this.log.info("########################## 4");
        this.log.info("-------- PRUEBAS");
        
       
        this.log.info("-------- PRUEBAS");
        this.log.info("/joinRoom - the message has been sent");
    }
    
    
    // NO se utiliza, reciben el aviso mediante suscripcion al recibir el controlador la
    //.. llamada para crear la room
    /*
    private void makeKnowThereIsANewRoom(Long roomId){//%%
        log.info(" * makeKnowThereIsANewRoom to...");
        this.roomsManager.printIncomingParticipants();//*
        
        Room room = new Room(this.roomsManager.getRoom(roomId));
        WSMessage wsmsg = new WSMessage("thereIsANewRoom", room);
        log.info("gson: {}", wsmsg.toString());
        List<UserSession> incomingParticipants = this.roomsManager.getIncomingParticipants();
        this.sendAMessageToUsers(wsmsg, incomingParticipants);
        
        log.info(" /makeKnowThereIsANewRoom - the message has been sent");
    }
    */
    
    /*    
    private void makeKnowTheParticipantsOfRoom (ParticipantSession newParticipant, Long roomId){
        log.info("{} RoomHandler.makeKnowTheParticipantsOfRoom {}{}{}", Info.START_SYMBOL, newParticipant.getName(), roomId, Hour.getTime());
        
        List<ParticipantSession> participants = this.roomsManager.getParticipantsByRoomId(roomId);
        
        JsonObject jsonAnswer = new JsonObject();
        jsonAnswer.addProperty("id", "thereIsAParticipant");
        
        for (ParticipantSession participant : participants){
            log.info("participant: {}", participant.toString());

            jsonAnswer.addProperty("userName", participant.getUserName());
            jsonAnswer.addProperty("name", participant.getName());
            jsonAnswer.addProperty("userType", participant.getUserType());

          //  newParticipant.sendMeAMessage(jsonAnswer);
         // UserFile  uf = participant.getUserImage();
          //WSMessage wsm = new WSMessage(ID_THERE_IS_A_PARTICIPANT, uf); 
            newParticipant.sendMeAMessage(new WSMessage(ID_THERE_IS_A_PARTICIPANT, participant));
        }
            
        log.info("{} RoomHandelr.makeKnowTheParticipantsOfRoom - the messages have been sent", Info.FINISH_SYMBOL, Hour.getTime());
    }

    */
    private void makeKnowTheParticipantsOfRoom (ParticipantSession newParticipant, Long roomId){
        log.info("{} RoomHandler.makeKnowTheParticipantsOfRoom {}{}{}", Info.START_SYMBOL, newParticipant.getName(), roomId, Hour.getTime());
        
        List<ParticipantSession> participants = this.roomsManager.getParticipantsByRoomId(roomId);
        
        newParticipant.sendMeAMessage(new WSMessage(ID_THESE_ARE_THE_PARTICIPANTS, participants));
        
            
        log.info("{} RoomHandelr.makeKnowTheParticipantsOfRoom - the messages have been sent", Info.FINISH_SYMBOL, Hour.getTime());
    }
    
    private void makeKnowThereIsANewParticipant(ParticipantSession newParticipant, Long roomId){
        log.info("{} MediaRoom.makeKnowThereIsANewParticipant ({}) to participants of room {} {}", Info.START_SYMBOL, newParticipant.toString(), roomId, Hour.getTime());
        //this.printTheRoomParticipants();
        List<ParticipantSession> participants = this.roomsManager.getParticipantsByRoomId(roomId);
        
        JsonObject jsonAnswer = new JsonObject();
        jsonAnswer.addProperty("id", "thereIsANewParticipant");
        jsonAnswer.addProperty("userName", newParticipant.getUserName());
        jsonAnswer.addProperty("name", newParticipant.getName());
        jsonAnswer.addProperty("userType", newParticipant.getUserType());
        
       log.info("Message to send to: {}",jsonAnswer.toString());
       
        for (ParticipantSession participant : participants){
            log.debug(roomId +":");
            log.debug("- " + participant.getUserName());
            // The new participant already knows he's in the room
            if (!participant.equals(newParticipant)){
                log.info("Student/Tutor: {} {}", participant.getUserName(), participant.getSession().getId());
               // participant.sendMeAMessage(jsonAnswer);
               participant.sendMeAMessage(new WSMessage(ID_THERE_IS_A_NEW_PARTICIPANT, newParticipant));
            }    
        }  
            
        log.info("{} MediaRoom.makeKnowThereIsANewParticipant - the messages have been sent {}", Info.FINISH_SYMBOL, Hour.getTime());
    }
    
    private synchronized void receiveVideoFrom(WebSocketSession session, OfferPayloadWSMessage offerPayloadWSMessage){
        this.log.info("<- receiveVideoFrom -> id: {}, message: {}", session.getId());
        this.log.info("");
        this.log.info("---------------------   RECEIVEVIDEOFROM      -------------------");
        this.log.info("");
             
        UserSession user = this.usersRegistry.getUserBySessionId(session.getId());

        if (user != null){

            String senderUserName = offerPayloadWSMessage.getUserName();
            String offerSdp = offerPayloadWSMessage.getOfferSdp();
            
            String answerSdp = this.roomsManager.manageOfferVideo(user.getUserName(), senderUserName, offerSdp);
            offerPayloadWSMessage.setAnswerSdp(answerSdp);
            WSMessage msg = new WSMessage(offerPayloadWSMessage.getIdForWsMessage(), offerPayloadWSMessage);
            
            user.sendMeAMessage(msg);

        }    
        
        this.log.info("/ receiveVideoFrom {} from {}", user, offerPayloadWSMessage.getUserName());    
    }
    
    private synchronized void receiveAddress(WebSocketSession session, IceCandidatePayloadWSMessage iceCandidatePayloadWSMessage){
        //this.log.info("* RoomHandler.receiveAddress <- iceCandidate: id: {}, message: {}",session.getId() , jsonMessage.toString());
        
        UserSession user = this.usersRegistry.getUserBySessionId(session.getId());

        if (user != null){

            String senderUserName = iceCandidatePayloadWSMessage.getUserName();
            IceCandidate iceCandidate = iceCandidatePayloadWSMessage.getIceCandidate();
            
            this.roomsManager.manageAddress(user.getUserName(), senderUserName, iceCandidate);
        }
        this.log.info("/ RoomHandler.receiveAddress"); 
    }
    
    /**
    * An user has left the room.
    */
    private synchronized void exitRoom (WebSocketSession session, String userName, Room room){
        this.log.info("<- %%%%% RoomHandler.exitRoom: id: {}, message: {} {}", session.getId(), userName, room.getId().toString());
        
        /*
        String userName = jsonMessage.get("userName").getAsString();
        Room room =this.gson.fromJson(jsonMessage.get("room"),Room.class);
        **/
        this.log.info("Se ha desereliciado la room");
        this.log.info("El id de la room es: {}", room.getId());

        Long roomId = room.getId();
        
        UserSession user = this.roomsManager.participantLeavesARoom(userName, roomId);

        if (user != null){
            this.log.info("** The participant {} has been got out from the room **", user.getUserName());
            
            if (!this.roomsManager.existRoom(roomId)){
                this.log.info("La room {} ya no existe", roomId);
                //this.makeKnowThereIsAnAvailableRoomLess(roomId); //*
                this.simpMessagingTemplate.convertAndSend("/eliminated_room", room);
            }
            else{
                this.makeKnowAParticipantHasLeftTheRoom(user, roomId);
            }
        }
        this.log.info("/ $$$$$ RoomHandler.exitRoom - it has finished");
    }
    
    /*
    private void makeKnowThereIsAnAvailableRoomLess(Long roomId){
        this.log.info("  * makeKnowThereIsARoomLess to...");
        /*
        JsonObject jsonAnswer = new JsonObject();
        jsonAnswer.addProperty("id", "thereIsAnAvailableRoomLess");
        jsonAnswer.addProperty("roomId", roomId);
        
        List<UserSession> incomingParticipants = this.roomsManager.getIncomingParticipants();
        this.sendAMessageToUsers(jsonAnswer, incomingParticipants);
        *//*
        List<UserSession> incomingParticipants = this.roomsManager.getIncomingParticipants();
        WSMessage wsm = new WSMessage(ID_AVAILABLE_ROOM_LESS, roomId);
        this.sendAMessageToUsers(wsm, incomingParticipants);
        
        this.log.info("  /makeKnowThereIsARoomLess - the message has been sent");
    }
     */


    private void makeKnowAParticipantHasLeftTheRoom(UserSession user, Long roomId){
        log.info("{} MediaRoom.makeKnowAParticipantHasLeftTheRoom: {} {}", Info.START_SYMBOL, user.getUserName(), Hour.getTime());
       
        List<ParticipantSession> participants = this.roomsManager.getParticipantsByRoomId(roomId);
        
        if (!participants.isEmpty()){
            
            String userName = user.getUserName();

            final List<UserSession> unnotifiedParticipants;
            
            JsonObject participantLeftJson = new JsonObject();
            participantLeftJson.addProperty("id", "aParticipantHasLeftTheRoom");
            participantLeftJson.addProperty("userName", userName);
            participantLeftJson.addProperty("userType", user.getUserType());
            participantLeftJson.addProperty("roomId", roomId);
            //unnotifiedParticipants =  this.sendAMessageToParticipants(participantLeftJson, participants);
            
            WSMessage msg = new WSMessage(ID_A_PARTICIPANT_HAS_LEFT_THE_ROOM, userName);
            unnotifiedParticipants =  this.sendAMessageToParticipants(msg, participants);
            
            for (final ParticipantSession participant : participants) {
                participant.receivesFarewellFrom(userName);
            }    
            
        }

        log.info("{} RoomHandler.makeKnowAParticipantHasLeftTheRoom - the messages have been sent {}", Info.FINISH_SYMBOL, Hour.getTime());
    }
    
    // Return the unnotified participants
    private List<UserSession> sendAMessageToUsers(JsonObject message, List<UserSession> users){
        log.info("{} sendAMessageToIncomingParticipants - message: {} {}",Info.START_SYMBOL, message, Hour.getTime());
        log.info("users: " + users.size());
       
        List<UserSession> unnotifiedUsers = new ArrayList<>();
        
        for (UserSession user : users){
            log.info("User: {}", user.getUserName());

            if (!user.sendMeAMessage(message)){
                unnotifiedUsers.add(user);
            }
        }  
        
        if (!unnotifiedUsers.isEmpty()) {
            log.debug("{} users don't have receive the message", unnotifiedUsers.size());
        }
        
        log.info("the message has been sent to all teh users");
        log.info("{} users don't have receive the message", unnotifiedUsers.size());
        Info.logInfoFinish("sendAMessageToIncomingParticipants");
        return unnotifiedUsers;
    }


    // Return the unnotified participants
    private List<UserSession> sendAMessageToUsers(WSMessage message, List<UserSession> users){
        log.info("{} sendAMessageToIncomingParticipants - message: {} {}",Info.START_SYMBOL, message, Hour.getTime());
        log.info("users: " + users.size());
       
        List<UserSession> unnotifiedUsers = new ArrayList<>();
        
        for (UserSession user : users){
            log.info("User: {}", user.getUserName());

            if (!user.sendMeAMessage(message)){
                unnotifiedUsers.add(user);
            }
        }  
        
        if (!unnotifiedUsers.isEmpty()) {
            log.debug("{} users don't have receive the message", unnotifiedUsers.size());
        }
        
        log.info("the message has been sent to all teh users");
        log.info("{} users don't have receive the message", unnotifiedUsers.size());
        Info.logInfoFinish("sendAMessageToIncomingParticipants");
        return unnotifiedUsers;
    }
    
    // Return the unnotified participants
    private List<UserSession> sendAMessageToParticipants(JsonObject message, List<ParticipantSession> participants){
        log.info("{} sendAMessageToIncomingParticipants - message: {} {}",Info.START_SYMBOL, message, Hour.getTime());
        log.info("IncomingParticipants: " + participants.size());
       
        List<UserSession> unnotifiedUsers = new ArrayList<>();
        
        if (!participants.isEmpty()){
            List<UserSession> users = new ArrayList<>();
            for (UserSession user : participants){
                users.add(user);
            } 
            unnotifiedUsers = sendAMessageToUsers(message, users);
        }
        
        log.info("the message has been sent to all the participants");
        log.info("{} users don't have receive the message", unnotifiedUsers.size());
        Info.logInfoFinish("sendAMessageToIncomingParticipants");
        return unnotifiedUsers;
    }

     // Return the unnotified participants
     private List<UserSession> sendAMessageToParticipants(WSMessage message, List<ParticipantSession> participants){
        log.info("{} sendAMessageToIncomingParticipants - message: {} {}",Info.START_SYMBOL, message, Hour.getTime());
        log.info("IncomingParticipants: " + participants.size());
       
        List<UserSession> unnotifiedUsers = new ArrayList<>();
        
        if (!participants.isEmpty()){
            List<UserSession> users = new ArrayList<>();
            for (UserSession user : participants){
                users.add(user);
            } 
            unnotifiedUsers = sendAMessageToUsers(message, users);
        }
        
        log.info("the message has been sent to all the participants");
        log.info("{} users don't have receive the message", unnotifiedUsers.size());
        Info.logInfoFinish("sendAMessageToIncomingParticipants");
        return unnotifiedUsers;
    }

    private void getRoomFiles(Long roomId, WebSocketSession session){
        log.info("* RoomHandler.getRoomFiles from room: {}", roomId);
        MediaRoom room = this.roomsManager.getRoom(roomId);
        List<UserFile> files = new ArrayList<>(); 
        if (room != null) {
            files.addAll(room.getFilesHistory());
        }
        WSMessage msg = new WSMessage(ID_AVAILABLE_FILES, files);
        SendMessage.toClient(msg, session);
        log.info("# RoomHandler.getRoomFiles");
    }

    public List<String> getTextMessageIdsICanHandle(){
        List<String> idsList = new ArrayList<>(Arrays.asList(this.ids));
        return idsList;   
    }
    
    
   
    
}
