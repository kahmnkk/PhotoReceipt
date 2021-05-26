// Modules
const express = require('express');
const router = express.Router();

// Common
const errors = require('@src/errors');

// Api
const SessionMgr = require('@src/api/sessionMgr');
const routerBoard = require('@src/api/board/router');

// Utils
const utils = require('@src/utils/utils');

const ROUTERS = {
    getList: 'getList',
    getDetail: 'getDetail',
    like: 'like',
};

async function index(req, res) {
    const reqKeys = {
        router: 'router',
        data: 'data',
    };
    const resKeys = {
        result: 'result',
    };

    const session = new SessionMgr(req, res);
    const body = session.body;

    try {
        let response = {};

        let reqRouter = body[reqKeys.router];
        let reqDto = body[reqKeys.data];
        if (reqDto == null) {
            throw utils.errorHandling(errors.invalidRequestData);
        }

        if (typeof reqDto != 'object') {
            reqDto = JSON.parse(reqDto);
        }

        let resDto = null;
        switch (reqRouter) {
            case ROUTERS.getList:
                resDto = await routerBoard.getList(reqDto);
                break;

            case ROUTERS.getDetail:
                resDto = await routerBoard.getDetail(reqDto);
                break;

            case ROUTERS.like:
                resDto = await routerBoard.like(reqDto);
                break;

            default:
                throw utils.errorHandling(errors.invalidRequestRouter);
        }

        if (resDto == null) {
            throw utils.errorHandling(errors.invalidResponseData);
        }

        response[resKeys.result] = resDto;
        session.send(response);
    } catch (err) {
        session.error(err);
    }
}

utils.setRoute(router, '/index', index);

module.exports = router;
