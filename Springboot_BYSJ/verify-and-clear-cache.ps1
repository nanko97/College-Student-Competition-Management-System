# ============================================
# 清除浏览器缓存并验证修复脚本
# ============================================

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  竞赛列表页面修复验证工具" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 1. 检查源文件是否已修复
Write-Host "[1/4] 检查源文件修复状态..." -ForegroundColor Yellow
$listVuePath = "src\main\resources\admin\admin\src\views\modules\jingsaixinxi\list.vue"

if (Test-Path $listVuePath) {
    $content = Get-Content $listVuePath -Raw
    
    # 检查是否包含修复后的代码
    if ($content -match 'this\.\$storage\.get\(''role''\)') {
        Write-Host "  ✓ 源文件已修复：list.vue 使用 this.`$storage.get('role')" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 源文件未修复：list.vue 仍然使用 `$storage.get('role')" -ForegroundColor Red
        Write-Host "  提示：请重新运行修复脚本" -ForegroundColor Yellow
    }
    
    # 检查是否移除了表格的 v-if 条件
    if ($content -match 'v-if="isAuth\(''jingsaixinxi'',''查看''\)"') {
        Write-Host "  ⚠ 表格仍有权限控制：建议移除 v-if 条件" -ForegroundColor Yellow
    } else {
        Write-Host "  ✓ 表格权限控制已移除" -ForegroundColor Green
    }
} else {
    Write-Host "  ✗ 文件不存在：$listVuePath" -ForegroundColor Red
}

Write-Host ""

# 2. 清除浏览器缓存提示
Write-Host "[2/4] 清除浏览器缓存..." -ForegroundColor Yellow
Write-Host ""
Write-Host "  请选择以下任一方法清除缓存：" -ForegroundColor Cyan
Write-Host ""
Write-Host "  方法 1：使用快捷键（推荐）" -ForegroundColor White
Write-Host "    1. 在浏览器中按 Ctrl + Shift + Delete" -ForegroundColor Gray
Write-Host "    2. 勾选'缓存的图片和文件'" -ForegroundColor Gray
Write-Host "    3. 时间范围选择'全部时间'" -ForegroundColor Gray
Write-Host "    4. 点击'清除数据'" -ForegroundColor Gray
Write-Host ""
Write-Host "  方法 2：强制刷新" -ForegroundColor White
Write-Host "    按 Ctrl + F5 或 Ctrl + Shift + R" -ForegroundColor Gray
Write-Host ""
Write-Host "  方法 3：使用无痕模式" -ForegroundColor White
Write-Host "    按 Ctrl + Shift + N 打开无痕窗口" -ForegroundColor Gray
Write-Host ""

# 3. 打开验证页面
Write-Host "[3/4] 准备打开验证页面..." -ForegroundColor Yellow
$verifyHtml = "verify-fix.html"

if (Test-Path $verifyHtml) {
    Write-Host "  验证页面路径：$verifyHtml" -ForegroundColor Green
    Write-Host ""
    Write-Host "  是否打开验证页面？(Y/N)" -ForegroundColor Cyan
    $response = Read-Host
    
    if ($response -eq 'Y' -or $response -eq 'y') {
        $fullPath = Join-Path (Get-Location) $verifyHtml
        Start-Process "file://$fullPath"
        Write-Host "  ✓ 已打开验证页面" -ForegroundColor Green
    }
} else {
    Write-Host "  ✗ 验证页面不存在" -ForegroundColor Red
}

Write-Host ""

# 4. 打开系统页面
Write-Host "[4/4] 打开系统页面..." -ForegroundColor Yellow
$systemUrl = "http://localhost:8080/springbootrd362/admin/dist/index.html"

Write-Host "  系统地址：$systemUrl" -ForegroundColor Cyan
Write-Host ""
Write-Host "  是否打开系统页面？(Y/N)" -ForegroundColor Cyan
$response = Read-Host

if ($response -eq 'Y' -or $response -eq 'y') {
    Start-Process $systemUrl
    Write-Host "  ✓ 已打开系统页面" -ForegroundColor Green
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  验证步骤" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  1. 清除浏览器缓存（按上述方法）" -ForegroundColor White
Write-Host "  2. 打开系统页面并登录" -ForegroundColor White
Write-Host "  3. 进入竞赛列表页面" -ForegroundColor White
Write-Host "  4. 按 F12 打开开发者工具，查看控制台" -ForegroundColor White
Write-Host "  5. 检查是否还有 `$storage 错误" -ForegroundColor White
Write-Host "  6. 尝试新增一条竞赛记录" -ForegroundColor White
Write-Host "  7. 查看列表是否刷新显示新数据" -ForegroundColor White
Write-Host ""
Write-Host "  预期结果：" -ForegroundColor Green
Write-Host "    ✓ 控制台无 `$storage 错误" -ForegroundColor Green
Write-Host "    ✓ 列表能正常显示数据" -ForegroundColor Green
Write-Host "    ✓ 新增功能正常工作" -ForegroundColor Green
Write-Host ""
Write-Host "  如果还有问题，请查看控制台的详细错误信息" -ForegroundColor Yellow
Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
