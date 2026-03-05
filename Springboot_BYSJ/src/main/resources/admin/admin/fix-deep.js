const fs = require('fs');
const path = require('path');

function replaceDeepSelector(dir) {
  const files = fs.readdirSync(dir);
  
  files.forEach(file => {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);
    
    if (stat.isDirectory()) {
      replaceDeepSelector(filePath);
    } else if (file.endsWith('.vue')) {
      let content = fs.readFileSync(filePath, 'utf8');
      const originalContent = content;
      
      // 替换所有 /deep/ 为 ::v-deep
      content = content.replace(/&? ?\/deep\//g, '::v-deep');
      
      if (content !== originalContent) {
        fs.writeFileSync(filePath, content, 'utf8');
        console.log(`Fixed: ${filePath}`);
      }
    }
  });
}

const srcDir = path.join(__dirname, 'src', 'views');
replaceDeepSelector(srcDir);
console.log('Done!');
