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
var http_1 = require("@angular/http");
var Subject_1 = require("rxjs/Subject");
var Observable_1 = require("rxjs/Observable");
var connection_service_1 = require("./connection.service");
var FileService = (function () {
    function FileService(http, connection) {
        this.http = http;
        this.connection = connection;
        this.uploadedFileEndPoint = "uploadedFile/shared"; // stomp
        this.uploadFilePath = 'upload';
        this.downloadFilePath = "download";
        this.userImagePath = 'userImage';
        this._sizeLimit = 1000000;
        console.log("");
        console.log("****** new FileService");
        this.uploadUserImageAddress = "" + this.connection.urlServer + this.uploadFilePath + "/" + this.userImagePath;
        this.sharedFiles$ = new Subject_1.Subject();
        this.sharedFiles$ = new Subject_1.Subject();
    }
    FileService.prototype.init = function (address) {
        console.log("FileService.init(" + address + ")");
        // console.log(`FileService - this.files:`, this.files); 
        this.uploadFileAddress = "" + this.connection.urlServer + this.uploadFilePath + "/" + address;
        this.shippingAddress = "/" + this.uploadedFileEndPoint + "/" + address;
        console.log(this.uploadFilePath);
        console.log(this.shippingAddress);
        this.stompClient = this.connection.stompOverWsClient;
        this.subscription = this.stompClient.subscribe(this.shippingAddress, this.getOnMessage());
    };
    Object.defineProperty(FileService.prototype, "sizeLimit", {
        get: function () {
            return this._sizeLimit;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FileService.prototype, "uploadFileUrl", {
        get: function () {
            return this.uploadFileAddress;
        },
        enumerable: true,
        configurable: true
    });
    FileService.prototype.getUploadUserImageUrl = function (userName) {
        return this.uploadUserImageAddress + "/" + userName;
    };
    FileService.prototype.getExistingFiles = function () {
        // Implementar llamada al servidor para que devuelva los archivos compartidos
        var existingFiles = [];
        return Observable_1.Observable.of(existingFiles);
    };
    FileService.prototype.getSharedFiles = function () {
        return this.sharedFiles$;
    };
    FileService.prototype.getOnMessage = function () {
        var _this = this;
        var onMessage = function (data) {
            console.log("FileService.onMessage: ", data);
            var file = JSON.parse(data.body);
            file.loadUrl = _this.connection.pathName + file.loadUrl;
            file.loadUrl = file.loadUrl.replace('//', '/');
            file.downloadUrl = _this.connection.pathName + file.downloadUrl;
            file.downloadUrl = file.downloadUrl.replace('//', '/');
            _this.sharedFiles$.next(file);
        };
        return onMessage;
    };
    FileService.prototype.getFileName = function (file) {
        return file;
    };
    FileService.prototype.destroy = function () {
        this.subscription.unsubscribe();
    };
    return FileService;
}());
FileService = __decorate([
    core_1.Injectable(),
    __metadata("design:paramtypes", [http_1.Http, connection_service_1.ConnectionService])
], FileService);
exports.FileService = FileService;
//# sourceMappingURL=file.service.js.map