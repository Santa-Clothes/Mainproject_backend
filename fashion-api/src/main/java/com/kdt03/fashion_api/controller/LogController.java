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
                        body { font-family: 'Inter', sans-serif; background-color: #050505; color: #a1a1aa; }
                        pre { font-family: 'JetBrains Mono', monospace; }
                        .log-line:hover { background-color: rgba(168, 85, 247, 0.1); }
                        .level-INFO { color: #a855f7; }
                        .level-ERROR { color: #f43f5e; font-weight: bold; }
                        .level-WARN { color: #fbbf24; }
                        .level-DEBUG { color: #2dd4bf; }
                        .scroll-custom::-webkit-scrollbar { width: 6px; }
                        .scroll-custom::-webkit-scrollbar-track { background: #111111; }
                        .scroll-custom::-webkit-scrollbar-thumb { background: #27272a; border-radius: 3px; }
                    </style>
                </head>
                <body class="p-8">
                    <div class="max-w-6xl mx-auto">
                        <header class="flex justify-between items-end mb-10 border-b border-zinc-900 pb-6">
                            <div>
                                <h1 class="text-4xl font-black text-white mb-2 tracking-tighter">API LOGS</h1>
                                <p class="text-zinc-500 font-medium">Real-time system monitoring control</p>
                            </div>
                            <div class="flex gap-3 items-center">
                                <button onclick="copyLogs()" class="bg-zinc-900 hover:bg-zinc-800 text-zinc-300 px-5 py-2.5 rounded-full text-sm font-bold transition-all border border-zinc-800 flex items-center gap-2">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>
                                    COPY
                                </button>
                                <button onclick="fetchLogs()" class="bg-purple-600 hover:bg-purple-500 text-white px-6 py-2.5 rounded-full text-sm font-bold transition-all shadow-lg shadow-purple-900/20">
                                    REFRESH
                                </button>
                                <div class="h-6 w-px bg-zinc-800 mx-2"></div>
                                <label class="flex items-center gap-2 text-xs font-bold text-zinc-500 uppercase tracking-widest cursor-pointer select-none">
                                    <input type="checkbox" id="autoRefresh" class="accent-purple-600 rounded border-zinc-800 bg-zinc-900" checked> auto
                                </label>
                            </div>
                        </header>

                        <div class="bg-zinc-950 border border-zinc-900 rounded-2xl overflow-hidden shadow-[0_20px_50px_rgba(0,0,0,0.5)]">
                            <div class="bg-zinc-900/50 px-5 py-3 flex justify-between items-center border-b border-zinc-900">
                                <div class="flex gap-6 text-[10px] font-bold uppercase tracking-[0.2em] text-zinc-500">
                                    <span class="flex items-center gap-2">
                                        <span class="w-1.5 h-1.5 rounded-full bg-purple-500 animate-pulse"></span>
                                        System: <span class="text-zinc-300">Active</span>
                                    </span>
                                    <span>Stream: fashion-api.log</span>
                                </div>
                                <div class="flex gap-1.5">
                                    <div class="w-2.5 h-2.5 rounded-full bg-zinc-800"></div>
                                    <div class="w-2.5 h-2.5 rounded-full bg-zinc-800"></div>
                                    <div class="w-2.5 h-2.5 rounded-full bg-zinc-800"></div>
                                </div>
                            </div>
                            <div id="logContainer" class="p-6 h-[650px] overflow-y-auto scroll-custom whitespace-pre-wrap text-[13px] leading-relaxed selection:bg-purple-500/30 selection:text-white mt-1">
                                <div class="animate-pulse text-zinc-700 italic">Initializing console...</div>
                            </div>
                        </div>
                    </div>

                    <script>
                        function copyLogs() {
                            const container = document.getElementById('logContainer');
                            const text = container.innerText;
                            navigator.clipboard.writeText(text).then(() => {
                                const btn = event.currentTarget;
                                const originalHtml = btn.innerHTML;
                                btn.innerHTML = '✅ COPIED';
                                btn.classList.add('border-purple-500', 'text-purple-500');
                                setTimeout(() => {
                                    btn.innerHTML = originalHtml;
                                    btn.classList.remove('border-purple-500', 'text-purple-500');
                                }, 2000);
                            });
                        }

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
