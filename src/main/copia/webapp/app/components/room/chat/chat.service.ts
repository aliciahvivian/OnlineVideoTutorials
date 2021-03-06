import { Injectable } from '@angular/core';

import { ConnectionService } from './../../../services/connection.service';
import { IUserInfo } from './../../../models/user';

import { ChatMessage } from './chatMessage/chatMessage.types';
import { HexColorGenerator } from './hexColorGenerator';


@Injectable()
export class ChatService{
    
    private chatEndPoint: string = `/chat`;
    
    private colorGenerator: HexColorGenerator;
    private messages: ChatMessage[];
    private participants: any[]; 

    private stompClient: any;
    private subscription: any; 
    private shippingAddress: string = `${this.chatEndPoint}/noticeBoard`; 
    private destinyAddress: string = `${this.chatEndPoint}/mailBox`; 


    constructor(private connection: ConnectionService) {
        console.log("");
        console.log("% new ChatService");
        this.colorGenerator = new HexColorGenerator();
        this.messages = [];
        this.participants = []; 
    }

    init(address: string): void {
        this.destinyAddress = `${this.destinyAddress}/${address}`;
        this.shippingAddress = `${this.shippingAddress}/${address}`;
        this.stompClient = this.connection.stompOverWsClient;
        this.subscription = this.stompClient.subscribe(this.shippingAddress, this.getOnMessage());
    }

    getMessages(): ChatMessage[] {
        return this.messages;
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

   public sendMessage(sender: IUserInfo, message: string) {
       let msg: ChatMessage = {
            sender: sender.userName,
            message: message,
            date: new Date().toLocaleTimeString(),
            color: undefined
        };
        console.log("Chat message sended: ", msg);
        this.stompClient.send(this.destinyAddress, {}, JSON.stringify(msg));

    }

   destroy(): void{
        this.subscription.unsubscribe();
        this.messages.length = 0;
    }

}
