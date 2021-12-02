# 									PONG GAME

## 1. Giới thiệu

Pong game (hay còn gọi là game bóng bàn) là ứng dụng chơi game được thiết kế dành cho 2 người chơi với mục đích giải trí, xả stress sau những giờ làm việc căng thẳng. Cách chơi rất đơn giản nên game phù hợp với mọi lứa tuổi.

Được code bằng ngôn ngữ Java, game đã được ứng dụng rất nhiều kiến thức trong môn Lập trình mạng như lập trình TCP, UDP, RMI,... để truyền/nhận dữ liệu. Điều đó thể hiện sự cố gắng áp dụng những kiến thức đã học vào thực tế của tác giả, mang lại giá trị cho game không chỉ về tính giải trí mà còn về tính học thuật.

## 2. Các chức năng trong Game:

### 2.1: Tạo / join Server (có xác thực mật khẩu)

- TCP 

### 2.2: Tìm kiếm server sử dụng UDP (broadcast)

- UDP

### 2.3: Kick người chơi theo ý muốn

- TCP

### 2.4: Trạng thái sẵn sàng / chưa sẵn sàng 

- TCP

### 2.5: Chọn số lượng bóng

- TCP

### 2.6: Chat

- TCP

### 2.7: Tạm dừng / tiếp tục khi chơi game

- RMI

## 3. Cách cài đặt:

Người dùng netbean chỉ cần clone về và chạy. Game được code trên netbean 8.2.

## 4. Cách sử dụng:

- Bước 1: Người chơi đóng vai trò **Server** chạy file **LanGameFrame**. Sau đó chọn **tạo Server** rồi nhập các thông tin về tên người chơi, tên phòng và mật khẩu.

- Bước 2: Người chơi đóng vai trò **Client** chạy file **LanGameFrame**. Sau đó chọn **join Server** rồi nhập các thông tin về tên người chơi và mật khẩu phòng.
- Bước 3: Khi **Server** và **Client** đã kết nối với nhau, người chơi có thể **chat**, chọn **số lượng bóng** và bắt đầu tham gia chơi.



