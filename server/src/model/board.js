// Common
const dbMgr = require('@src/database/dbMgr');
const querys = require('@src/querys');
const errors = require('@src/errors');

// Model
const BaseModel = require('@src/model/baseModel');

// Utils
const utils = require('@src/utils/utils');
const logger = require('@src/utils/logger');
const time = require('@src/utils/time');

const boardFrame = {
    idx: null,
    owner: null,
    imgLink: null,
    filter: null,
    text: null,
    status: null,
};
const boardLikeFrame = {
    boardIdx: null,
    userIdx: null,
    status: null,
};

const likeStatus = {
    like: 0,
    unlike: 1,
};

const genKey = 'gen:board';

class Board extends BaseModel {
    constructor(boardIdx) {
        super();

        this.boardIdx = boardIdx;

        this.CACHE_KEY = 'KEY_BOARD_INFO:'.concat(this.boardIdx);
        this.CACHE_LIKE_KEY = 'KEY_BOARD_LIKE:'.concat(this.boardIdx);
        this.CACHE_RANKING_KEY = 'KEY_BOARD_RANKING';
    }

    get likeStatus() { return likeStatus; } // prettier-ignore

    /**
     * @override
     * @returns {boardFrame}
     */
    getFrame(initData = null) {
        const rtn = super.getFrame(boardFrame, initData);
        return rtn;
    }

    /**
     * @override
     * @returns {boardLikeFrame}
     */
    getLikeFrame(initData = null) {
        const rtn = super.getFrame(boardLikeFrame, initData);
        return rtn;
    }

    /**
     *
     * @param {Number} boardIdx
     * @returns {boardFrame}
     */
    async getBoardInfo(boardIdx) {
        const [result] = await dbMgr.getFromCache(dbMgr.mysqlConn.master, 'KEY_BOARD_INFO:' + boardIdx, this.querySelectBoard(boardIdx));
        return result;
    }

    /**
     *
     * @param {Array<Number>} boardIdxes
     * @returns {Array<boardFrame>}
     */
    async getBoardInfos(boardIdxes) {
        let rtn = [];
        for (let i in boardIdxes) {
            const result = await dbMgr.getFromCache(dbMgr.mysqlConn.master, 'KEY_BOARD_INFO:' + boardIdxes[i], this.querySelectBoard(boardIdxes[i]));
            rtn.push(result);
        }
        return rtn;
    }

    async createBoardInfo(owner, imgLink, filter, text) {
        let rtn = {
            board: this.getFrame(),
            query: dbMgr.getQueryFrame(),
        };

        let boardObj = this.getFrame();
        boardObj.idx = await this.genIdx();
        boardObj.owner = owner;
        boardObj.imgLink = imgLink;
        boardObj.filter = filter;
        boardObj.text = text;
        boardObj.status = 0;

        rtn.board = boardObj;

        rtn.query.querys.push(this.queryInsertBoard(boardObj));
        rtn.query.cmds.push(['hset', 'KEY_BOARD_INFO:' + boardObj.idx, boardObj.idx, JSON.stringify(boardObj)]);

        rtn.query.cmds.push(['zadd', this.CACHE_RANKING_KEY, 0, boardObj.idx]);

        return rtn;
    }

    async createBoardLike(userIdx, status, likeCount) {
        let rtn = {
            like: this.getLikeFrame(),
            query: dbMgr.getQueryFrame(),
        };

        let likeObj = this.getLikeFrame();
        likeObj.boardIdx = this.boardIdx;
        likeObj.userIdx = userIdx;
        likeObj.status = status;

        rtn.like = likeObj;

        rtn.query.querys.push(this.queryInsertBoardLike(likeObj));
        rtn.query.cmds.push(['hset', this.CACHE_LIKE_KEY, userIdx, JSON.stringify(likeObj)]);

        let count = likeCount;
        if (status == likeStatus.like) {
            count++;
        } else {
            count--;
        }

        rtn.query.cmds.push(['zadd', this.CACHE_RANKING_KEY, count, likeObj.boardIdx]);

        return rtn;
    }

    async getBoardRankingList() {
        const rtn = await dbMgr.redis.master.client.zrevrange(this.CACHE_RANKING_KEY, 0, -1, 'WITHSCORES');
        return rtn;
    }

    async getBoardLikeCount() {
        const rtn = await dbMgr.redis.master.client.zscore(this.CACHE_RANKING_KEY, this.boardIdx);
        return Number(rtn);
    }

    /**
     *
     * @param {Number} boardIdx
     * @returns {Array<boardLikeFrame>}
     */
    async getBoardLikeInfo(boardIdx) {
        const result = await dbMgr.getFromCache(dbMgr.mysqlConn.master, this.CACHE_LIKE_KEY, this.querySelectBoardLike(boardIdx));
        return result;
    }

    // Private
    async genIdx() {
        return await dbMgr.redis.gen.client.incrby(genKey, 1);
    }

    querySelectBoard(boardIdx) {
        return dbMgr.mysql.user.makeQuery(querys.master.selectBoardInfo, boardIdx);
    }

    querySelectBoardLike(boardIdx) {
        return dbMgr.mysql.user.makeQuery(querys.master.selectBoardLikeInfo, boardIdx);
    }

    /**
     *
     * @param {boardFrame} boardObj
     * @returns {String}
     */
    queryInsertBoard(boardObj) {
        return dbMgr.mysql.user.makeQuery(querys.master.insertBoardInfo, boardObj.idx, boardObj.owner, boardObj.imgLink, JSON.stringify(boardObj.filter), boardObj.text, boardObj.status);
    }

    /**
     *
     * @param {boardLikeFrame} boardLikeObj
     * @returns {String}
     */
    queryInsertBoardLike(boardLikeObj) {
        return dbMgr.mysql.user.makeQuery(querys.master.insertBoardLikeInfo, boardLikeObj.boardIdx, boardLikeObj.userIdx, boardLikeObj.status, boardLikeObj.status);
    }
}

module.exports = Board;
