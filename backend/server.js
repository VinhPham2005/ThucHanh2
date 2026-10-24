const express = require('express');
const mysql = require('mysql2');

const app = express();
app.use(express.json());

// Thiết lập cấu hình kết nối MySQL
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',         // Điền username MySQL của bạn
    password: 'Vinh1234@', // Điền password MySQL của bạn
    database: 'thuchanh2'
});

// Kết nối database
db.connect((err) => {
    if (err) {
        console.error('Lỗi kết nối database:', err);
        return;
    }
    console.log('Đã kết nối thành công tới MySQL');
});

// Tạo API endpoint để lấy danh sách user
app.get('/api/users', (req, res) => {
    const sql = 'SELECT * FROM users';
    db.query(sql, (err, results) => {
        if (err) {
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

// Khởi chạy server
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server đang chạy tại http://localhost:${PORT}`);
});