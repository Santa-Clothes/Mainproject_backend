package com.kdt03.fashion_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @org.springframework.beans.factory.annotation.Value("${logging.file.path:C:/fashion-api-logs}")
    private String logPath;

    private String getLogFilePath() {
        return logPath + "/fashion-api.log";
    }

    // 로그 대시보드 HTML 반환
    @GetMapping("/view")
    public ResponseEntity<String> logDashboard() {
        String html = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Fashion API - Log Dashboard</title>
                    <script src="https://cdn.tailwindcss.com"></script>
                    <style>
                        @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono&family=Inter:wght@400;600&display=swap');
                        body { font-family: 'Inter', sans-serif; background-color: #0f172a; color: #e2e8f0; }
                        pre { font-family: 'JetBrains Mono', monospace; }
                        .log-line:hover { background-color: rgba(255, 255, 255, 0.05); }
                        .level-INFO { color: #3b82f6; }
                        .level-ERROR { color: #ef4444; font-weight: bold; }
                        .level-WARN { color: #f59e0b; }
                        .level-DEBUG { color: #10b981; }
                        .scroll-custom::-webkit-scrollbar { width: 8px; }
                        .scroll-custom::-webkit-scrollbar-track { background: #1e293b; }
                        .scroll-custom::-webkit-scrollbar-thumb { background: #475569; border-radius: 4px; }
                    </style>
                </head>
                <body class="p-6">
                    <div class="max-w-6xl mx-auto">
                        <header class="flex justify-between items-center mb-8">
                            <div>
                                <h1 class="text-3xl font-bold text-white mb-2">🚀 Log Dashboard</h1>
                                <p class="text-slate-400">Fashion API 실시간 로그 모니터링</p>
                            </div>
                            <div class="flex gap-4">
                                <button onclick="fetchLogs()" class="bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded-lg font-semibold transition-colors">
                                    새로고침
                                </button>
                                <label class="flex items-center gap-2 text-sm text-slate-300">
                                    <input type="checkbox" id="autoRefresh" class="rounded border-slate-700 bg-slate-800" checked> 자동 갱신 (3초)
                                </label>
                            </div>
                        </header>

                        <div class="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden shadow-2xl">
                            <div class="bg-slate-800 px-4 py-2 flex justify-between items-center border-b border-slate-700">
                                <div class="flex gap-4 text-xs font-semibold uppercase tracking-wider text-slate-400">
                                    <span>Status: <span class="text-green-400">Online</span></span>
                                    <span>File: logs/fashion-api.log</span>
                                </div>
                                <div class="flex gap-2">
                                    <span class="w-3 h-3 rounded-full bg-red-500"></span>
                                    <span class="w-3 h-3 rounded-full bg-yellow-500"></span>
                                    <span class="w-3 h-3 rounded-full bg-green-500"></span>
                                </div>
                            </div>
                            <div id="logContainer" class="p-4 h-[600px] overflow-y-auto scroll-custom whitespace-pre-wrap text-sm leading-relaxed">
                                <div class="animate-pulse text-slate-500 italic">로그를 불러오는 중...</div>
                            </div>
                        </div>
                    </div>

                    <script>
                        function fetchLogs() {
                            fetch('/api/logs/raw')
                                .then(res => res.text())
                                .then(data => {
                                    const container = document.getElementById('logContainer');
                                    const lines = data.split('\\n');
                                    const formatted = lines.map(line => {
                                        let cls = 'log-line block';
                                        if (line.includes('INFO')) cls += ' level-INFO';
                                        if (line.includes('ERROR')) cls += ' level-ERROR';
                                        if (line.includes('WARN')) cls += ' level-WARN';
                                        if (line.includes('DEBUG')) cls += ' level-DEBUG';
                                        return `<span class="${cls}">${line}</span>`;
                                    }).join('');

                                    const isScrolledToBottom = container.scrollHeight - container.clientHeight <= container.scrollTop + 50;
                                    container.innerHTML = formatted;
                                    if (isScrolledToBottom) {
                                        container.scrollTop = container.scrollHeight;
                                    }
                                });
                        }

                        fetchLogs();
                        setInterval(() => {
                            if (document.getElementById('autoRefresh').checked) {
                                fetchLogs();
                            }
                        }, 3000);
                    </script>
                </body>
                </html>
                """;
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    // 원본 로그 데이터 반환
    @GetMapping("/raw")
    public ResponseEntity<String> rawLogs() throws IOException {
        List<String> lastLines = Files.readAllLines(Paths.get(getLogFilePath()));
        // 너무 많으면 브라우저가 힘드니 마지막 1000줄만 전송
        int limit = 1000;
        String result = lastLines.stream()
                .skip(Math.max(0, lastLines.size() - limit))
                .collect(Collectors.joining("\n"));

        return ResponseEntity.ok(result);
    }
}
