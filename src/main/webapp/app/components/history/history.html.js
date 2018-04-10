"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * @author Juan Antonio Echeverrías Aranda (juanan.echeve@gmail.com)
 */
exports.historyTemplate = "<div id=\"ovt-history\" class=\"animate join\"> \n    <div class=\"ovt-header ovt-full-name\">Historial</div>\n        <div class=\"ovt-container\">\n            <div class=\"ovt-container-col\">\n                <div class=\"header\">    \n                    <div class=\"ovt-subheader\" *ngIf=\"rooms && rooms.length === 0\">No has participado en ninguna tutor\u00EDa todav\u00EDa</div>\n                    <div class=\"ovt-subheader\" *ngIf=\"rooms && rooms.length > 0\">Tutor\u00EDas en las que has participado</div>\n                    <div>Mostrar\n                        <select #roomsNumber>\n                            <option value=\"5\">5</option>\n                            <option value=\"10\">10</option>\n                            <option value=\"15\">15</option>\n                            <option value=\"-1\">Todas</option>\n                        </select>\n                </div>\n                </div>\n                <div class=\"ovt-rooms\">\n             \n                    <ul>\n                        <li  *ngFor=\"let room of rooms | limitTo:roomsNumber.value\">\n                        <!--\n                        <div class=\"ovt-selectable\" [class.selectedRoom]=\"selectedRoom==room\" [detailsSelected]=\"selectedRoom==room\" (click)=\"onSelectedRoom(room)\">{{room.name}} ({{room.getCreatedAtTimeStamp()}})\n                            <ovt-room-details *ngIf=\"selectedRoom == room\" [room]=\"room\"></ovt-room-details>\n                        </div>\n                        -->\n                        \n                        <ovt-room-details [ngClass]=\"'ovt-room-details'\" [detailsSelected]=\"selectedRoom==room\" (click)=\"onSelectedRoom(room, event)\" [room]=\"room\"></ovt-room-details>\n \n                        </li>\n                    </ul>\n                </div>\n            </div>\n            <div class=\"ovt-out\">\n                <button class=\"btn ovt-btn ovt-inline\" (click)=\"onReturn()\">Volver</button>\n            </div>\n        </div>        \n </div>";
//# sourceMappingURL=history.html.js.map