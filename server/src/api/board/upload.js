// Modules
const express = require('express');
const router = express.Router();

// Common
const upload = require('@src/utils/multer');
const errors = require('@src/errors');
const dbMgr = require('@src/database/dbMgr');

// Api
const SessionMgr = require('@src/api/sessionMgr');

// Model
const Board = require('@src/model/board');

// Utils
const utils = require('@src/utils/utils');

router.post('/upload', upload.single('image'), async (req, res) => {
    const reqKeys = {
        filter: 'filter',
        text: 'text',
    };
    const resKeys = {
        result: 'result',
    };

    const imageLink = req.file.location;

    const session = new SessionMgr(req, res);
    const body = session.body;
    try {
        let response = {};

        const userIdx = session.getIdx();
        if (userIdx == null) {
            throw utils.errorHandling(errors.sessionWrongAccess);
        }

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
