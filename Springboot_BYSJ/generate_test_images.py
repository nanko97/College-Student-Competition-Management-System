# ============================================
# 大学生竞赛管理系统 - 测试数据生成器
# ============================================
# 功能：生成真实的头像图片和竞赛封面图片
# 使用：python generate_images.py
# 输出：upload/student_*.jpg, upload/teacher_*.jpg, upload/contest_*.jpg
# ============================================

import os
import random
from PIL import Image, ImageDraw, ImageFont

# 创建输出目录
os.makedirs('upload', exist_ok=True)

# 颜色配置
STUDENT_COLORS = ['#4CAF50', '#2196F3', '#FF9800', '#E91E63', '#9C27B0']
TEACHER_COLORS = ['#3F51B5', '#009688', '#795548', '#607D8B', '#F44336']
CONTEST_COLORS = ['#FF5722', '#00BCD4', '#8BC34A', '#FFC107', '#673AB7']

# 文字配置
CHINESE_NAMES_STUDENT = [
    '张伟', '李娜', '王强', '刘芳', '陈杰', '杨敏', '赵磊', '孙丽', '周杰', '吴婷',
    '郑浩', '冯雪', '何军', '蒋琳', '沈涛', '韩梅', '杨梅', '田宇', '董建', '袁慧'
]
CHINESE_NAMES_TEACHER = [
    '赵建国', '钱晓敏', '孙志强', '李晓红', '周伟', '吴芳', '郑建华', '冯涛', '陈静', '褚卫'
]
CONTEST_NAMES = [
    '程序设计竞赛', '数学建模竞赛', '英语竞赛', '电子设计竞赛', '互联网 +',
    '挑战杯', '智能汽车', '物联网设计', '人工智能大赛', '广告艺术大赛'
]

def create_avatar(name, color, filename, size=(200, 200)):
    """创建头像图片"""
    img = Image.new('RGB', size, color=color)
    draw = ImageDraw.Draw(img)
    
    # 尝试加载中文字体
    try:
        font = ImageFont.truetype("simhei.ttf", 60)  # 黑体
    except:
        try:
            font = ImageFont.truetype("msyh.ttc", 60)  # 微软雅黑
        except:
            font = ImageFont.load_default()
    
    # 绘制文字
    text = name[0]  # 取第一个字
    text_width = draw.textlength(text, font=font)
    position = ((size[0] - text_width) / 2, (size[1] - 80) / 2)
    draw.text(position, text, fill='white', font=font)
    
    # 保存
    img.save(filename, quality=95)
    print(f'✓ 已生成：{filename}')

def create_contest_cover(contest_name, color, filename, size=(800, 400)):
    """创建竞赛封面图片"""
    img = Image.new('RGB', size, color=color)
    draw = ImageDraw.Draw(img)
    
    # 尝试加载中文字体
    try:
        font = ImageFont.truetype("simhei.ttf", 48)
    except:
        try:
            font = ImageFont.truetype("msyh.ttc", 48)
        except:
            font = ImageFont.load_default()
    
    # 绘制渐变效果
    for i in range(size[1]):
        alpha = int(255 * (i / size[1]))
        draw.line([(0, i), (size[0], i)], fill=(alpha, alpha, alpha))
    
    # 绘制文字
    text = contest_name
    text_width = draw.textlength(text, font=font)
    position = ((size[0] - text_width) / 2, (size[1] - 60) / 2)
    draw.text(position, text, fill='white', font=font)
    
    # 添加装饰圆圈
    draw.ellipse([50, 50, 150, 150], outline='white', width=3)
    draw.ellipse([size[0]-150, 50, size[0]-50, 150], outline='white', width=3)
    draw.ellipse([50, size[1]-150, 150, size[1]-50], outline='white', width=3)
    draw.ellipse([size[0]-150, size[1]-150, size[0]-50, size[1]-50], outline='white', width=3)
    
    # 保存
    img.save(filename, quality=90)
    print(f'✓ 已生成：{filename}')

# 生成学生头像
print('\n=== 生成学生头像 ===')
for i, name in enumerate(CHINESE_NAMES_STUDENT, 1):
    color = random.choice(STUDENT_COLORS)
    filename = f'upload/student_{i:03d}.jpg'
    create_avatar(name, color, filename)

# 生成教师头像
print('\n=== 生成教师头像 ===')
for i, name in enumerate(CHINESE_NAMES_TEACHER, 1):
    color = random.choice(TEACHER_COLORS)
    filename = f'upload/teacher_{i:03d}.jpg'
    create_avatar(name, color, filename)

# 生成竞赛封面
print('\n=== 生成竞赛封面 ===')
for i, name in enumerate(CONTEST_NAMES, 1):
    color = random.choice(CONTEST_COLORS)
    filename = f'upload/contest_{i:03d}.jpg'
    create_contest_cover(name, color, filename)

print('\n✓ 所有图片生成完成！')
print(f'  - 学生头像：{len(CHINESE_NAMES_STUDENT)} 张')
print(f'  - 教师头像：{len(CHINESE_NAMES_TEACHER)} 张')
print(f'  - 竞赛封面：{len(CONTEST_NAMES)} 张')
print('\n提示：如需要更精美的图片，可以使用以下在线工具：')
print('  - 头像生成：https://www.thispersondoesnotexist.com/')
print('  - 封面图片：https://unsplash.com/')
print('  - 图标素材：https://www.iconfont.cn/')
