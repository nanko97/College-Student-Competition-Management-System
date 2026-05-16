package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = {"com.dao"})
@EnableScheduling // 启用定时任务
public class SpringbootSchemaApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootSchemaApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
		return applicationBuilder.sources(SpringbootSchemaApplication.class);
	}

	/**
	 * 启动成功后输出提示信息
	 */
	@Bean
	public org.springframework.boot.CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("");
			System.out.println("╔════════════════════════════════════════════════════════╗");
			System.out.println("║                                                        ║");
			System.out.println("║        🎉 大学生竞赛管理系统启动成功！🎉              ║");
			System.out.println("║                                                        ║");
			System.out.println("╚════════════════════════════════════════════════════════╝");
			System.out.println("");
			System.out.println("📌 系统信息：");
			System.out.println("   • 后端服务: http://localhost:9090/BYSJ_Springboot");
			System.out.println("   • API文档:  http://localhost:9090/BYSJ_Springboot/swagger-ui.html");
			System.out.println("   • 前端服务: http://localhost:8081");
			System.out.println("");
			System.out.println("💡 提示：");
			System.out.println("   • 管理员账号: admin / 123456");
			System.out.println("   • 学生账号: 2022001 / 123456");
			System.out.println("   • 教师账号: T2022001 / 123456");
			System.out.println("   • 所有账号初始密码均为: 123456");
			System.out.println("");
			System.out.println("════════════════════════════════════════════════════════");
			System.out.println("");
		};
	}
}
