<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>JSP 업로드 테스트</title>
</head>
<body>
    <h2>📸 이미지 업로드 테스트</h2>
    
    <form id="uploadForm">
        <input type="file" name="file" id="fileInput" />
        <button type="button" onclick="uploadFile()">전송</button>
    </form>
    
    <hr />
    <h3>이미지</h3>
    <div id="result">
        <img id="preview" src="" style="max-width: 400px; display: none; border-radius: 10px;" />
    </div>

    <script>
        async function uploadFile() {
            const fileInput = document.getElementById('fileInput');
            const file = fileInput.files[0];

            if (!file) {
                alert("사진 고르세요");
                return;
            }


            const formData = new FormData();
            formData.append('file', file);

            try {

                const response = await fetch('http://10.125.121.182:8080/api/imageupload/upload', {
                    method: 'POST',
                    body: formData
                });

                const data = await response.json();

                if (data.success) {
                    const img = document.getElementById('preview');
                    img.src = data.imageUrl;
                    img.style.display = 'block';
                    alert("업로드 완료");
                } else {
                    alert("실패 " + data.message);
                }
            } catch (error) {
                console.error(error);
                alert("실패");
            }
        }
    </script>
</body>
</html>