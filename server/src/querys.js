const query = {
    master: {
        timestamp: 'SELECT UNIX_TIMESTAMP(NOW(3)) AS now, TIMESTAMPDIFF(SECOND, UTC_TIMESTAMP(), NOW()) AS offset',

        // tb_account_info
        selectAccountById: 'SELECT idx, id, pw, salt, status FROM tb_account_info WHERE id = ?',
        insertAccountInfo: 'INSERT INTO tb_account_info (idx, id, pw, salt, status) VALUES (?, ?, ?, ?, ?)',

        // tb_board_info
        selectBoardInfo: 'SELECT idx, owner, imgLink, filter, text, status FROM tb_board_info WHERE idx = ?',
        insertBoardInfo: 'INSERT INTO tb_board_info (idx, owner, imgLink, filter, text, status) VALUES (?, ?, ?, ?, ?, ?)',

        // tb_board_like
        selectBoardLikeInfo: 'SELECT boardIdx, userIdx, status FROM tb_board_like WHERE boardIdx = ?',
        insertBoardLikeInfo: 'INSERT INTO tb_board_like (boardIdx, userIdx, status) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE status = ?',
    },
    user: {
        // tb_user_info
        selectUserInfo: 'SELECT idx, nickname, profile, UNIX_TIMESTAMP(createTime), UNIX_TIMESTAMP(updateTime) FROM tb_user_info WHERE idx = ?',
        insertUserInfo: 'INSERT INTO tb_user_info (idx, nickname, createTime) VALUES (?, ?, ?)',
    },
};

module.exports = query;
