// Modules
const express = require('express');
const router = express.Router();

// Common
const upload = require('@src/utils/multer');
const errors = require('@src/errors');
const dbMgr = require('@src/database/dbMgr');
const config = require('@root/config');

// Api
const SessionMgr = require('@src/api/sessionMgr');

// Model
const Board = require('@src/model/board');

// Utils
const utils = require('@src/utils/utils');

router.post('/upload', upload.single('image'), async (req, res) => {
    const reqKeys = {
        userIdx: 'userIdx',
        filter: 'filter',
        text: 'text',
    };
    const resKeys = {
        result: 'result',
    };

    const link = req.file.location;
    let linkArr = link.split('/');
    let imageFileName = linkArr[linkArr.length - 1];
    const imageLink = config.cdnUrl + imageFileName;

    const session = new SessionMgr(req, res);
    const body = session.body;
    try {
        let response = {};

        const userIdx = Number(body[reqKeys.userIdx]);
        const reqFilter = body[reqKeys.filter];
        const reqText = body[reqKeys.text];

        const board = new Board();
        const createResult = await board.createBoardInfo(userIdx, imageLink, JSON.parse(reqFilter), reqText);

        await dbMgr.set(dbMgr.mysqlConn.master, createResult.query);

        response[resKeys.result] = true;
        session.send(response);
    } catch (err) {
        session.error(err);
    }
});

module.exports = router;
