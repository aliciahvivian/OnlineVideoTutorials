/*
 * @author Juan Antonio Echeverrías Aranda (juanan.echeve@gmail.com)
 */
 export const historyTemplate = `<div id="ovt-history" class="animate join"> 
    <div class="ovt-header ovt-full-name">Historial</div>
        <div class="ovt-container">
            <div class="ovt-container-col ovt-rooms">
                <div class="header">    
                    <div class="ovt-subheader" *ngIf="rooms && rooms.length === 0">No has participado en ninguna tutoría todavía</div>
                    <div class="ovt-subheader" *ngIf="rooms && rooms.length > 0">Tutorías en las que has participado</div>
                </div>
                <div class="ovt-rooms">
                    <ul class="ovt-none-list-syle">
                        <li class="ovt-selectable ovt-room-selector"  *ngFor="let room of rooms">
                        <div [class.selectedRoom]="selectedRoom==room" (click)="onSelectedRoom(room)">{{room.name}} ({{room.getCreatedAtTimeStamp()}})</div>
                        <ovt-room-details *ngIf="selectedRoom == room" [room]="room"></ovt-room-details>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="ovt-out">
                <button class="btn ovt-btn ovt-inline" (click)="onReturn()">Volver</button>
            </div>
        </div>        
 </div>` 