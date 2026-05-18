<template>
	<el-main class="main-container">
		<div class="content-wrapper">
			<router-view class="router-view" />
		</div>
	</el-main>
</template>
<script>
	import menu from "@/utils/menu";
	export default {
		data() {
			return {
				menuList: [],
				role: "",
				currentIndex: -2,
				itemMenu: []
			};
		},
		mounted() {
			let menus = menu.list();
			this.menuList = menus;
			this.role = this.$storage.get("role");
		},
		methods: {
			menuHandler(menu) {
				this.$router.push({
					name: menu.tableName
				}).catch(err => {
					if (err.name !== 'NavigationDuplicated') {
						console.error('路由跳转失败:', err)
					}
				});
			},
			titleChange(index, menus) {
				this.currentIndex = index
				this.itemMenu = menus;
				console.log(menus);
			},
			homeChange(index) {
				this.itemMenu = [];
				this.currentIndex = index
				this.$router.push({
					name: 'Home'
				}).catch(err => {
					if (err.name !== 'NavigationDuplicated') {
						console.error('路由跳转失败:', err)
					}
				});
			},
			centerChange(index) {
				this.itemMenu = [{
					"buttons": ["新增", "查看", "修改", "删除"],
					"menu": "修改密码",
					"tableName": "updatePassword"
				}, {
					"buttons": ["新增", "查看", "修改", "删除"],
					"menu": "个人信息",
					"tableName": "center"
				}];
				this.currentIndex = index
				this.$router.push({
					name: 'Home'
				}).catch(err => {
					if (err.name !== 'NavigationDuplicated') {
						console.error('路由跳转失败:', err)
					}
				});
			}
		}
	};
</script>
<style lang="scss" scoped>
	@import "@/styles/variables";
	
	a {
		text-decoration: none;
		color: #555;
	}

	a:hover {
		background: #00c292;
	}

	.nav-list {
		width: 100%;
		margin: 0 auto;
		text-align: left;
		margin-top: 20px;

		.nav-title {
			display: inline-block;
			font-size: 15px;
			color: #333;
			padding: 15px 25px;
			border: none;
		}

		.nav-title.active {
			color: #555;
			cursor: default;
			background-color: #fff;
		}
	}

	.nav-item {
		margin-top: 20px;
		background: #FFFFFF;
		padding: 15px 0;

		.menu {
			padding: 15px 25px;
		}
	}

	.main-container {
		padding: 60px 0px 0px 230px;
		background: $bg-primary;
		min-height: 100vh;
		overflow-y: auto;
		
		.content-wrapper {
			min-height: calc(100vh - 60px);
			background: transparent;
			padding: $spacing-lg;
			margin: 0;
		}
	}
</style>
