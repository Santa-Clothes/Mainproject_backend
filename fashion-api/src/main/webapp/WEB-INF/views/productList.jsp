<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>📦 상품 목록</title>
    <style>
        body { font-family: 'Malgun Gothic', sans-serif; padding: 20px; color: #333; }
        h2 { border-bottom: 2px solid #333; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: center; }
        th { background-color: #f8f9fa; }
        tr:hover { background-color: #f1f1f1; }
        .btn-load { padding: 10px 20px; cursor: pointer; background: #333; color: #fff; border: none; border-radius: 4px; }
        .btn-load:hover { background: #555; }
        /* 이미지 크기를 정갈하게 맞추는 킥입니다.. */
        .prod-img { width: 100px; height: 100px; object-fit: cover; border-radius: 4px; border: 1px solid #eee; }
    </style>
</head>
<body>

    <h2>📦 우리 창고 상품 목록</h2>
    
    <button class="btn-load" onclick="loadProducts()">목록 다시 불러오기</button>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>이미지</th>
                <th>상품명</th>
                <th>가격</th>
                <th>카테고리</th>
            </tr>
        </thead>
        <tbody id="productTableBody">
            <tr><td colspan="5">준비 중입니다..</td></tr>
        </tbody>
    </table>

    <script>
        async function loadProducts() {
            const tableBody = document.getElementById('productTableBody');
            if (!tableBody) return;

            try {
                const response = await fetch('/api/products/list');
                if (!response.ok) throw new Error("서버 응답 오류");
                
                const products = await response.json();
                tableBody.innerHTML = '';

                if (products.length === 0) {
                    tableBody.innerHTML = '<tr><td colspan="5">창고가 비었습니다.</td></tr>';
                    return;
                }

                products.forEach(p => {
                    
                    const imgSrc = `/images/\${p.productId}.png`;
                    
                    const row = `<tr>
                        <td>\${p.productId}</td>
                        <td>
                            <img src="\${imgSrc}" alt="\${p.productName}" class="prod-img" 
                                 onerror="this.src='https://via.placeholder.com/100?text=No+Image'">
                        </td>
                        <td>\${p.productName || '이름 없음'}</td>
                        <td>\${p.price ? p.price.toLocaleString() : 0}원</td>
                        <td>\${p.category ? p.category.categoryId : '-'}</td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });

            } catch (error) {
                console.error("에러 발생:", error);
                tableBody.innerHTML = '<tr><td colspan="5" style="color:red;">데이터를 불러오는 데 실패했습니다.</td></tr>';
            }
        }

        document.addEventListener('DOMContentLoaded', loadProducts);
    </script>
</body>
</html>