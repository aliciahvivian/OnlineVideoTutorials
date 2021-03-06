import { Injectable, EventEmitter } from '@angular/core';

import { Message } from '../models/types';

@Injectable()
export class HandlerService{

    private handlers: Map<string, EventEmitter<Message>>;

    constructor(){
        console.log("*HandlerService constructor");
        this.handlers = new Map<string, EventEmitter<Message>>();
    }

    attach(id: string, ee: EventEmitter<Message>):void{
        console.log(`HandlerService.attach ${id}`);
        this.handlers.set(id, ee);
    }

    detach(id: string): void{
        console.log(`HandlerService.detach ${id}`);
        this.handlers.delete(id);
    }

    handle(message: any): boolean{
        console.log("HandlerService.handle: ", message);
        let parsedMessage: Message = JSON.parse(message.data);
        let idMessage: string = parsedMessage.id;
        console.log(`HandlerService.handler ${idMessage}`);
        console.log(`message`, message);
        
        let ee: EventEmitter<Message> = this.handlers.get(idMessage);
        try {
            console.log(`ee${idMessage}.next`);
            ee.next(parsedMessage);
            return true;
        }
        catch(e){
            console.log(`Can't find a handler for ${idMessage}`);
            return false;
        }    
    }
}