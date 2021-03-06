package com.yxy.market1.controller;

import com.yxy.market1.Utils.ResultUtil;
import com.yxy.market1.controller.base.BaseController;
import com.yxy.market1.entity.Notice;
import com.yxy.market1.entity.Product;
import com.yxy.market1.entity.dto.form.ProductForm;
import com.yxy.market1.entity.dto.response.ProductResponse;
import com.yxy.market1.entity.dto.response.Result;
import com.yxy.market1.service.INoticeService;
import com.yxy.market1.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController extends BaseController {
    @Autowired
    private IProductService productService;
    @Autowired
    private INoticeService noticeService;

//    @PostMapping("/releaseproduct")
//    public String createProduct(HttpServletRequest request, Model model, ProductForm productForm) {
//        Product product = new Product();
//        product.setCategory(productForm.getCatagory());
//        Date date = new Date();
//        product.setDate(date);
//        product.setDescription(productForm.getDecription());
//        product.setName(productForm.getName());
//
//        return "product-details";
//    }

    @PostMapping("/releaseproduct")
    @ResponseBody
    public Result<Integer> upLoadProduct(HttpServletRequest request, ProductForm productForm) {
        System.out.println("in here");
        MultipartFile photo = productForm.getPhoto();
        if (photo == null) {
            return ResultUtil.fail("选择要上传的文件！");
        }
        if (photo.getSize() > 1024 * 1024 * 10) {
            return ResultUtil.fail("文件大小不能超过10M！");
        }
//        String suffix = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".") + 1, photo.getOriginalFilename().length());
        String suffix = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".") + 1);
        if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
            return ResultUtil.fail("请选择jpg,jpeg,gif,png格式的图片！");
        }
        String savePath = "/home/yxy/project_upload/";
        File savePathFile = new File(savePath);
        if (!savePathFile.exists()) {
            //若不存在该目录，则创建目录
            savePathFile.mkdir();
        }
        String filename = new Date().getTime() + "." + suffix;
        try {
            //将文件保存指定目录
            photo.transferTo(new File(savePath + filename));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.fail("保存文件异常");
        }
        Product product = new Product();
        product.setCategory(productForm.getCategory());
        product.setPrice(productForm.getPrice());
        Date date = new Date();
        product.setDate(date);
        product.setDescription(productForm.getDescription());
        product.setName(productForm.getName());
        product.setSellerid(productForm.getSellerId());
        String prefix = "/storage/";
        product.setPictureAddr(prefix + filename);

        product.setStatus("已发布");
        product = productService.createProduct(product);
        String content = "发布了商品"+product.getName();
        Notice notice = new Notice(Integer.valueOf(productForm.getSellerId()), date, content,"未读");
        noticeService.addNotice(notice);
//        System.out.println(product.getId());
//        return ResultUtil.success(savePath + filename);
        return ResultUtil.success(product.getId());
    }

    @GetMapping("/product-details/{id}")
    public String productDetailView(HttpServletRequest request, Model model, @PathVariable Integer id) throws Exception {
        Product product = productService.findProductById(id);
        addModelAtt(model, "product", product);
        return "product-details";
    }


    // TODO
    @GetMapping("/precheck/{id}")
    public String productCheck(HttpServletRequest request, Model model, @PathVariable Integer id) throws Exception {
        Product product = productService.findProductById(id);
        addModelAtt(model, "product", product);
        return "precheck";
    }

    @GetMapping("/shop/{id}")
    public String shopToGo(HttpServletRequest request,@PathVariable Integer id) {
        return "shop-"+id;
    }

    @PostMapping("/display_goods")
    @ResponseBody
    public Result<List<ProductResponse>> getProductList(HttpServletRequest request) {
        List<Product> productList = productService.findAllProduct();
        return getListResult(productList);
    }

    @PostMapping("/category_product")
    @ResponseBody
    public Result<List<ProductResponse>> getProductListByCategory(HttpServletRequest request, String category) {
        List<Product> productList = productService.findProductsByCategory(category);
        return getListResult(productList);
    }

    @PostMapping("/namelike_product")
    @ResponseBody
    public Result<List<ProductResponse>> getProductListByNameLike(HttpServletRequest request, String namelike) {
        System.out.println(namelike);
        List<Product> productList = productService.findProductByNameLike(namelike);
//        System.out.println(productList.size());
        return getListResult(productList);
    }

    private Result<List<ProductResponse>> getListResult(List<Product> productList) {
        productList.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product p : productList) {
            if (p.getStatus().equals("已发布")) {
                productResponses.add(new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getPictureAddr()));
            }
        }
        return ResultUtil.success(productResponses);
    }

//    @PostMapping("/userlogin.f")
//    public String fFrontUserLogin(HttpServletRequest request, Model model, UserLoginForm loginForm, BindingResult bindingResult) throws Exception {
//        System.out.println("this func is called");
//        System.out.println("username is " + loginForm.getUsername());
//        System.out.println("username is " + loginForm.getPassword());
//        if (bindingResult.hasErrors()) {
//            List<ObjectError> errors = bindingResult.getAllErrors();
//            addModelAtt(model, VIEW_MSG, errors.get(0).getDefaultMessage());
//            return "login-register";
//        }
//        User user = mUserService.loginAuthentication(loginForm);
//        if (null != user) {
////            System.out.println("login now");
//            mUserService.joinSession(request, user);
//            return "redirect:/";
//        }
//        addModelAtt(model, VIEW_LABEL, 1);
//        addModelAtt(model, VIEW_MSG, "用户名或密码错误");
//        return "login-register";
//    }
}
