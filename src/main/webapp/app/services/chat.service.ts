import { Injectable } from '@angular/core';

import { ConnectionService } from './connection.service';
import { ChatMessage } from '../models/types';

import { UserService } from './user.service';

import { HexColorGenerator } from './../models/hexColorGenerator';


@Injectable()
export class ChatService{
    
    private chatEndPoint: string = `/chat`;
    
    private colorGenerator: HexColorGenerator;
    private messages: ChatMessage[];
    private participants: any[]; 

    private stompClient: any;
    private subscription: string = `${this.chatEndPoint}/noticeBoard`; 
    private destiny: string = `${this.chatEndPoint}/mailBox`; 


    constructor(private connection: ConnectionService, private me: UserService) {
        console.log("");
        console.log("% new ChatService");
        this.colorGenerator = new HexColorGenerator();
        this.messages = [];
        this.participants = []; 
    }

    init(address: string): void{
        this.destiny = `${this.destiny}/${address}`;
        this.subscription = `${this.subscription}/${address}`;
        this.connect();
    }

    getMessages(): ChatMessage[] {
        return this.messages;
    }

    private connect(){
        console.log("* ChatService.connect");

        this.stompClient = this.connection.stompOverWsClient;
        this.stompClient.subscribe(this.subscription, this.getOnMessage());
        
        console.log(this.stompClient);
    }

    private getOnMessage(): any{
        let onMessage = (message) => {
            console.log(`Msg received: ${message}`);
            this.publish(JSON.parse(message.body));
        }

        return onMessage;
    }


    public publish(message: ChatMessage): void {
        this.addColor(message);
        this.messages.push(message);
    }

    private addColor(message: ChatMessage): void {
        if (!this.participants[message.sender]) {
            this.participants[message.sender] = this.colorGenerator.getAColor();
        }
        message.color = this.participants[message.sender];
    }

   public sendMessage(message: string) {
       let myUserName = this.me.myUserName;
       let msg: ChatMessage = {
            sender: myUserName,
            message: message,
            date: new Date().toLocaleTimeString(),
            color: undefined
        };
        this.stompClient.send(this.destiny, {}, JSON.stringify(msg));

    }

    private disconnect(): void{

        if (this.stompClient !== null){
            this.stompClient.disconnect();
        }
    }

    destroy(): void{
        this.disconnect();
        this.messages.length = 0;
    }

}
