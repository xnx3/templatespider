package com.xnx3.util;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.commons.lang3.StringUtils;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimHash {
    private String tokens; //字符串
    private BigInteger strSimHash;//字符产的hash值
    private int hashbits = 64; // 分词后的hash数;


    public SimHash(String tokens) {
        this.tokens = tokens;
        this.strSimHash = this.simHash();
    }

    public SimHash(String tokens, int hashbits) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.strSimHash = this.simHash();
    }


    /**
     * 清除html标签
     * @param content
     * @return
     */
    private String cleanResume(String content) {
        // 若输入为HTML,下面会过滤掉所有的HTML的tag
//        content = StringUtils.lowerCase(content);
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&nbsp;"};
        for (String s : strings) {
            content = content.replaceAll(s, "");
        }
        return content;
    }


    /**
     * 这个是对整个字符串进行hash计算
     * @return
     */
    private BigInteger simHash() {

        tokens = cleanResume(tokens); // cleanResume 删除一些特殊字符

        int[] v = new int[this.hashbits];

        List<Term> termList = StandardTokenizer.segment(this.tokens); // 对字符串进行分词

        //对分词的一些特殊处理 : 比如: 根据词性添加权重 , 过滤掉标点符号 , 过滤超频词汇等;
        Map<String, Integer> weightOfNature = new HashMap<String, Integer>(); // 词性的权重
        weightOfNature.put("n", 2); //给名词的权重是2;
        Map<String, String> stopNatures = new HashMap<String, String>();//停用的词性 如一些标点符号之类的;
        stopNatures.put("w", ""); //
        int overCount = 5; //设定超频词汇的界限 ;
        Map<String, Integer> wordCount = new HashMap<String, Integer>();

        for (Term term : termList) {
            String word = term.word; //分词字符串

            String nature = term.nature.toString(); // 分词属性;
            //  过滤超频词
            if (wordCount.containsKey(word)) {
                int count = wordCount.get(word);
                if (count > overCount) {
                    continue;
                }
                wordCount.put(word, count + 1);
            } else {
                wordCount.put(word, 1);
            }

            // 过滤停用词性
            if (stopNatures.containsKey(nature)) {
                continue;
            }

            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                int weight = 1;  //添加权重
                if (weightOfNature.containsKey(nature)) {
                    weight = weightOfNature.get(nature);
                }
                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的所有特征的向量和
                    v[i] += weight;
                } else {
                    v[i] -= weight;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }


    /**
     * 对单个的分词进行hash计算;
     * @param source
     * @return
     */
    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            /**
             * 当sourece 的长度过短，会导致hash算法失效，因此需要对过短的词补偿
             */
            while (source.length() < 3) {
                source = source + source.charAt(0);
            }
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    /**
     * 计算海明距离,海明距离越小说明越相似;
     * @param other
     * @return
     */
    public int hammingDistance(SimHash other) {
        BigInteger m = new BigInteger("1").shiftLeft(this.hashbits).subtract(
                new BigInteger("1"));
        BigInteger x = this.strSimHash.xor(other.strSimHash).and(m);
        int tot = 0;
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }


    public double getSemblance(SimHash s2 ){
        double i = (double) this.hammingDistance(s2);
        return 1 - i/this.hashbits ;
    }

    public static void main(String[] args) {

        String s1 = "  借鉴hashmap算法找出可以hash的key值，因为我们使用的simhash是局部敏感哈希，这个算法的特点是只要相似的字符串只有个别的位数是有差别变化。那这样我们可以推断两个相似的文本，至少有16位的simhash是一样的。具体选择16位、8位、4位，大家根据自己的数据测试选择，虽然比较的位数越小越精准，但是空间会变大。分为4个16位段的存储空间是单独simhash存储空间的4倍。之前算出5000w数据是 382 Mb，扩大4倍1.5G左右，还可以接受：）  通过这样计算，我们的simhash查找过程全部降到了1毫秒以下。就加了一个hash效果这么厉害？我们可以算一下，原来是5000w次顺序比较，现在是少了2的16次方比较，前面16位变成了hash查找。后面的顺序比较的个数是多少？ 2^16 = 65536， 5000w/65536 = 763 次。。。。实际最后链表比较的数据也才 763次！所以效率大大提高！  到目前第一点降到3.6毫秒、支持5000w数据相似度比较做完了。还有第二点同一时刻发出的文本如果重复也只能保留一条和短文本相识度比较怎么解决。其实上面的问题解决了，这两个就不是什么问题了。  之前的评估一直都是按照线性计算来估计的，就算有多线程提交相似度计算比较，我们提供相似度计算服务器也需要线性计算。比如同时客户端发送过来两条需要比较相似度的请求，在服务器这边都进行了一个排队处理，一个接着一个，第一个处理完了在处理第二个，等到第一个处理完了也就加入了simhash库。所以只要服务端加了队列，就不存在同时请求不能判断的情况。 simhash如何处理短文本？换一种思路，simhash可以作为局部敏感哈希第一次计算缩小整个比较的范围，等到我们只有比较700多次比较时，就算使用我们之前精准度高计算很慢的编辑距离也可以搞定。当然如果觉得慢了，也可以使用余弦夹角等效率稍微高点的相似度算法";
        String s2 = "  1、分词，把需要判断文本分词形成这个文章的特征单词。 最后形成去掉噪音词的单词序列并为每个词加上权重，我们假设权重分为5个级别（1~5）。只要相似的字符串只有个别的位数是有差别变化。那这样我们可以推断两个相似的文本， 比如：“ 美国“51区”雇员称内部有9架飞碟，曾看见灰色外星人 ” ==> 分词后为 “ 美国（4） 51区（5） 雇员（3） 称（1） 内部（2） 有（1） 9架（3） 飞碟（5） 曾（1） 看见（3） 灰色（4） 外星人（5）”， 括号里是代表单词在整个句子里重要程度，数字越大越重要。 2、hash，通过hash算法把每个词变成hash值， 比如“美国”通过hash算法计算为 100101, “51区”通过hash算法计算为 101011。 这样我们的字符串就变成了一串串数字，还记得文章开头说过的吗，要把文章变为数字计算才能提高相似度计算性能，现在是降维过程进行时。 3、加权，通过 2步骤的hash生成结果，需要按照单词的权重形成加权数字串， 比如“美国”的hash值为“100101”，通过加权计算为“4 -4 -4 4 -4 4”；“51区”的hash值为“101011”，通过加权计算为 “ 5 -5 5 -5 5 5”。 4、合并，把上面各个单词算出来的序列值累加，变成只有一个序列串。 比如 “美国”的 “4 -4 -4 4 -4 4”，“51区”的 “ 5 -5 5 -5 5 5”， 把每一位进行累加， “4+5 -4+-5 -4+5 4+-5 -4+5 4+5” ==》 “9 -9 1 -1 1 9”。 这里作为示例只算了两个单词的，真实计算需要把所有单词的序列串累加。 5、降维，把4步算出来的 “9 -9 1 -1 1 9” 变成 0 1 串，形成我们最终的simhash签名。 如果每一位大于0 记为 1，小于0 是统优先公司";
        String s3 = "  算法找出可以hash的key值，因为我们使用的simhash是局部敏感哈希，这个算法的特点是只要相似的字 把需要判断文本分词形成这个文章的特征单词。 最后形成去掉噪音词的只要相似的字符串只有个别的位数是有差别变化。那这样我们可以推断两个相似的文本，单词序分词是代表单词在整个句子里重要程度，数字越大越重要。  2、hash，通过hash算法把每个词变成hash值， 比如“美国”通过hash算法计算为 100101, “51区”通过hash算法计算为 101011。 这样我们的字符串就变成了一串串数字，还记得文章开头说过的吗，要把文章变为数字加权，通过 家可能会有疑问，经过这么多步骤搞这么麻烦，不就是为了得到个 0 1 字符串吗？我直接把这个文本作为字符串输入v较，前面16位变成了hash查找。后面的顺序比较的个数是多，用hd5是用于生成唯一签来相差甚远；hashmap也是用于键值对查找，便于快速插入和查找的数据结构。不过我们主要解决的是文本相似度计算，要比较的是两个文章是否相识，当然我们降维生成了hashcode也是用于这个目的。看到这里估计大家就明白了，我们使用的sim是这样的，传统hash函数解决的是生成唯一值，比如 md5、hashmap等。md5是用于生成唯一签名串，只要稍微多加一个字符md5的两个数字看起来相差甚远；hashmap也是用于键值对查找，便于快速插入和查找的数据结构。不过我们主要解决的是文本相似度计算，要比较的是两个文章是否相识，当然我们降维生成了hashcode也是用于这个目的。看到这里估计大家就明白了，我们使用的simhash就算把文章中的字符串变成 01 串也还是可以用于计算相似度的，而传统的hashcode却不行。我们可以来做个测试，两个相差只有一个字符的文本串，“你妈妈喊你回家吃饭哦，回家罗回家罗” 和 “你妈妈叫你回家吃饭啦，回家罗回家罗”。短文本大量重复信息不会被过滤，是不是";
        String s4 = "  最后形成去掉噪音词的单词序分词是代表单词在整个句子里重要程度，数字越大越重要。 最后形成去掉噪音词的单词序列并为每个词加上权重 2、hash，通过hash算法把每个词变成hash值， 比如“美国”通过hash算法计算为 100101, “51区”通过hash算法计算为 101011。 这样我们的字符串就变成了一串串数字，还记得文章开头说过的吗，分为4个16位段的存储空间是单独simhash存储空间的4倍。之前算出5000w数据是 382 Mb，扩大4倍1.5G左右，还可以接受：） 要把文章变为数字加权，通过 家可能会有疑问，经过这么多步骤搞这么麻烦，不就是为了得到个 0 1 字符串吗？我直接把这个文本作为字符串输入，用hd5是用于生成唯一签来相差甚远；hashmap也是用于键值对查找，便于快速插入和查找的数据结构。不过我们主要解决的是文本相似度计算，要比较的是两个文章是否相识，当然我们降维生成了hashcode也是用于这个目的。看到这里估计大家就明白了，我们使用的sim是这样的，传统hash函数解决的是生成唯一值，比如 md5、hashmap等。md5是用于生成唯一签名串，只要稍微多加一个字符md5的两个数字看起来相差甚远；hashmap也是用于键值对查找，便于快速插入和查找的数据结构。不过我们主要解决的是文本相似度计算，要比较的是两个文章是否相识，当然我们降维生成了hashcode也是用于这个目的。看到这里估计大家就明白了，我们使用的simhash就算把文章中的字符串变成 01 串也还是可以用于计算相似度的，而传统的hashcode却不行。我们可以来做个测试，两个相差只有一个字符的文本串，“你妈妈喊你回家吃饭哦，回家罗回家罗” 和 “你妈妈叫你回家吃饭啦，回家罗回家罗”。短文本大量重复信息不会被过滤，";
        long l3 = System.currentTimeMillis();
        SimHash hash1 = new SimHash(s1, 64);
        SimHash hash2 = new SimHash(s2, 64);
        SimHash hash3 = new SimHash(s3, 64);
        SimHash hash4 = new SimHash(s4, 64);
        System.out.println("======================================");
        System.out.println(  hash1.hammingDistance(hash2) );
        System.out.println(  hash2.hammingDistance(hash3) );
        System.out.println(  hash4.hammingDistance(hash3) );
        System.out.println(  hash1.getSemblance(hash3) );
        System.out.println(  hash2.getSemblance(hash3) );
        System.out.println(  hash3.getSemblance(hash4) );
        long l4 = System.currentTimeMillis();
        System.out.println(l4-l3);
        System.out.println("======================================");



    }
}
