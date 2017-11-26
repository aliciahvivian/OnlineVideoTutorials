/**
 * @author Juan Antonio Echeverrías Aranda (juanan.echeve@gmail.com)
 *
 */
"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var router_1 = require("@angular/router");
var waitingRoom_service_1 = require("./waitingRoom.service");
var user_service_1 = require("../../services/user.service");
var waitingRoom_html_1 = require("./waitingRoom.html");
var room_1 = require("./../../models/room");
var WaitingRoomComponent = (function () {
    function WaitingRoomComponent(waitingRoom, router, me) {
        this.waitingRoom = waitingRoom;
        this.router = router;
        this.me = me;
        console.log("");
        console.log("% WaitingRoom constructor " + new Date().toLocaleTimeString());
        console.log("Last room: ", this.me.getMyLastRoom());
        this.roomName = this.me.userName;
        console.log("/ WaitingRoom constructor " + new Date().toLocaleTimeString());
        console.log("");
    }
    WaitingRoomComponent.prototype.ngOnInit = function () {
        console.log("WaitingRoomComponent.onInit");
        console.log(this.me);
        this.getAvailableRooms();
        if (this.me.amIAStudent()) {
            this.waitingRoom.initAsStudent(this.me.getMe());
        }
        else {
            console.log("Im a tutor");
            console.log("My current room: ", this.me.getMyCurrentRoom());
            this.waitingRoom.initAsTutor(this.me.getMe());
        }
    };
    WaitingRoomComponent.prototype.getAvailableRooms = function () {
        var _this = this;
        this.waitingRoom.getAvailableRooms()
            .subscribe(function (availableRooms) {
            console.log("available rooms: ", availableRooms);
            _this.availableRooms = availableRooms;
        });
    };
    WaitingRoomComponent.prototype.onCreateRoom = function (roomName) {
        var _this = this;
        this.waitingRoom.createRoom(roomName, this.me.userName)
            .subscribe(function (room) {
            _this.enterIntoRoom(room);
        }, function (error) { return console.log(error); });
    };
    /* // EL nombre de la room será su id
        private createRoomName(roomName : string): string{
            let name = roomName;
            if (name !== ""){
                name = name.replace(this.me.userName, "");
                name = name.replace(" ", "_");
            }
            return name === "" ? `${this.me.userName}` : `${this.me.userName }_${name }`;
        }
    */
    WaitingRoomComponent.prototype.onJoinRoom = function (room) {
        var _this = this;
        console.log("");
        console.log("* WaitingRoom.joinRoom: " + room.name + " " + new Date().toLocaleTimeString());
        if (this.me.amIATutor()) {
            var auxRoom = new room_1.Room();
            auxRoom.setDataRoom(room);
            this.waitingRoom.retrieveRoom(auxRoom.json())
                .subscribe(function (room) {
                _this.enterIntoRoom(room);
            }, function (error) { return console.log(error); });
        }
        else {
            this.enterIntoRoom(room);
        }
        console.log("/ WaitingRoom.joinRoom " + new Date().toLocaleTimeString());
        console.log("");
    };
    WaitingRoomComponent.prototype.enterIntoRoom = function (room) {
        console.log('enterIntoRoom');
        console.log(room);
        this.me.registerCurrentRoom(room);
        console.log(this.me.getMyCurrentRoom());
        this.router.navigate(['/rooms', room.id]);
    };
    WaitingRoomComponent.prototype.onSignOut = function () {
        console.log("");
        console.log("* <- WaitingRoom.onLogOut " + new Date().toLocaleTimeString());
        this.router.navigate(['/sign']);
    };
    WaitingRoomComponent.prototype.ngOnDestroy = function () {
        console.log("");
        console.log("* <- WaitingRoom.ngOnDestroy " + new Date().toLocaleTimeString());
        if (this.me.amIAStudent()) {
            this.waitingRoom.destroyAsStudent(this.me.getMe());
        }
        else {
            this.waitingRoom.destroyAsTutor(this.me.getMe());
        }
        console.log("/ WaitingRoom.ngOnDestroy " + new Date().toLocaleTimeString());
        console.log("");
    };
    return WaitingRoomComponent;
}());
WaitingRoomComponent = __decorate([
    core_1.Component({
        moduleId: module.id,
        selector: 'ovt-waitingRoom',
        styleUrls: ["waitingRoom.css"],
        template: waitingRoom_html_1.waitingRoomTemplate,
        providers: [waitingRoom_service_1.WaitingRoomService]
    }),
    __metadata("design:paramtypes", [waitingRoom_service_1.WaitingRoomService, router_1.Router, user_service_1.UserService])
], WaitingRoomComponent);
exports.WaitingRoomComponent = WaitingRoomComponent;
//# sourceMappingURL=waitingRoom.component.js.map